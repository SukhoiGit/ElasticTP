package com.artemis.elastictp.config.nacos.cloud.starter.refresher;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.artemis.elastictp.core.executor.ElasticTpRegistry;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorHolder;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorProperties;
import com.artemis.elastictp.core.executor.support.BlockingQueueTypeEnum;
import com.artemis.elastictp.core.executor.support.RejectedPolicyTypeEnum;
import com.artemis.elastictp.core.toolkit.ThreadPoolExecutorBuilder;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.parser.ConfigParserHandler;
import com.artemis.elastictp.spring.base.support.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Nacos Cloud 版本刷新处理器
 */
@Slf4j
@RequiredArgsConstructor
public class NacosCloudRefresherHandler implements ApplicationRunner {

    private final BootstrapConfigProperties properties;
    private ConfigService configService;

    public static final String CHANGE_THREAD_POOL_TEXT = "[{}] \uD83E\uDDF5 Dynamic thread pool parameter changed:"
            + "\n    corePoolSize: {}"
            + "\n    maximumPoolSize: {}"
            + "\n    capacity: {}"
            + "\n    keepAliveTime: {}"
            + "\n    rejectedType: {}"
            + "\n    allowCoreThreadTimeOut: {}";
    public static final String CHANGE_DELIMITER = "%s => %s";
    @Override
    public void run(ApplicationArguments args) throws Exception {
        configService = ApplicationContextHolder.getBean(NacosConfigProperties.class).configServiceInstance();
        registerListener();
    }

    @SneakyThrows
    public void registerListener() {
        BootstrapConfigProperties.NacosConfig nacosConfig = properties.getNacos();
        configService.addListener(
                nacosConfig.getDataId(),
                nacosConfig.getGroup(),
                new Listener() {

                    @Override
                    public Executor getExecutor() {
                        return ThreadPoolExecutorBuilder.builder()
                                .corePoolSize(1)
                                .maximumPoolSize(1)
                                .keepAliveTime(9999L)
                                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                                .threadFactory("clod-nacos-refresher-thread_")
                                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                                .build();
                    }

                    @SneakyThrows
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        Map<Object, Object> configInfoMap = ConfigParserHandler.getInstance().parseConfig(configInfo, properties.getConfigFileType());
                        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfoMap);
                        Binder binder = new Binder(sources);
                        BootstrapConfigProperties refresherProperties = binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(properties)).get();

                        // 检查远程配置文件是否包含线程池配置
                        if (CollUtil.isEmpty(refresherProperties.getExecutors())) {
                            return;
                        }

                        // 刷新动态线程池对象核心参数
                        for (ThreadPoolExecutorProperties remoteProperties : refresherProperties.getExecutors()) {
                            // 检查线程池配置是否发生变化（与当前内存中的配置对比）
                            boolean changed = hasThreadPoolConfigChanged(remoteProperties);
                            if (!changed) {
                                continue;
                            }

                            // 将远程配置应用到线程池，更新相关参数
                            updateThreadPoolFromRemoteConfig(remoteProperties);

                            // 线程池参数变更后进行日志打印
                            String threadPoolId = remoteProperties.getThreadPoolId();
                            ThreadPoolExecutorHolder holder = ElasticTpRegistry.getHolder(threadPoolId);
                            ThreadPoolExecutorProperties originalProperties = holder.getExecutorProperties();
                            holder.setExecutorProperties(remoteProperties);
                            log.info(CHANGE_THREAD_POOL_TEXT,
                                    threadPoolId,
                                    String.format(CHANGE_DELIMITER, originalProperties.getCorePoolSize(), remoteProperties.getCorePoolSize()),
                                    String.format(CHANGE_DELIMITER, originalProperties.getMaximumPoolSize(), remoteProperties.getMaximumPoolSize()),
                                    String.format(CHANGE_DELIMITER, originalProperties.getQueueCapacity(), remoteProperties.getQueueCapacity()),
                                    String.format(CHANGE_DELIMITER, originalProperties.getKeepAliveTime(), remoteProperties.getKeepAliveTime()),
                                    String.format(CHANGE_DELIMITER, originalProperties.getRejectedHandler(), remoteProperties.getRejectedHandler()),
                                    String.format(CHANGE_DELIMITER, originalProperties.getAllowCoreThreadTimeOut(), remoteProperties.getAllowCoreThreadTimeOut()));
                        }
                    }
                });

        log.info("Dynamic thread pool refresher, add nacos cloud listener success. data-id: {}, group: {}", nacosConfig.getDataId(), nacosConfig.getGroup());
    }

    private boolean hasThreadPoolConfigChanged(ThreadPoolExecutorProperties remateProperties) {
        String threadPoolId = remateProperties.getThreadPoolId();
        ThreadPoolExecutorHolder holder = ElasticTpRegistry.getHolder(threadPoolId);
        if (holder == null) {
            log.warn("No thread pool found for thread pool id: {}", threadPoolId);
            return false;
        }
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties originalProperties = holder.getExecutorProperties();

        return hasDifference(originalProperties, remateProperties, executor);
    }

    private void updateThreadPoolFromRemoteConfig(ThreadPoolExecutorProperties remoteProperties) {
        String threadPoolId = remoteProperties.getThreadPoolId();
        ThreadPoolExecutorHolder holder = ElasticTpRegistry.getHolder(threadPoolId);
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties originalProperties = holder.getExecutorProperties();

        Integer remoteCorePoolSize = remoteProperties.getCorePoolSize();
        Integer remoteMaximumPoolSize = remoteProperties.getMaximumPoolSize();
        if (remoteCorePoolSize != null && remoteMaximumPoolSize != null) {
            int originalMaximumPoolSize = executor.getMaximumPoolSize();
            if (remoteCorePoolSize > originalMaximumPoolSize) {
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
                executor.setCorePoolSize(remoteCorePoolSize);
            } else {
                executor.setCorePoolSize(remoteCorePoolSize);
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
            }
        } else {
            if (remoteMaximumPoolSize != null) {
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
            }
            if (remoteCorePoolSize != null) {
                executor.setCorePoolSize(remoteCorePoolSize);
            }
        }

        if (remoteProperties.getAllowCoreThreadTimeOut() != null &&
                !Objects.equals(remoteProperties.getAllowCoreThreadTimeOut(), originalProperties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(remoteProperties.getAllowCoreThreadTimeOut());
        }

        if (remoteProperties.getRejectedHandler() != null &&
                !Objects.equals(remoteProperties.getRejectedHandler(), originalProperties.getRejectedHandler())) {
            RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy(remoteProperties.getRejectedHandler());
            executor.setRejectedExecutionHandler(handler);
        }

        if (remoteProperties.getKeepAliveTime() != null &&
                !Objects.equals(remoteProperties.getKeepAliveTime(), originalProperties.getKeepAliveTime())) {
            executor.setKeepAliveTime(remoteProperties.getKeepAliveTime(), TimeUnit.SECONDS);
        }
    }

    private boolean hasDifference(ThreadPoolExecutorProperties originalProperties, ThreadPoolExecutorProperties remateProperties, ThreadPoolExecutor executor) {
        return isChanged(originalProperties.getCorePoolSize(), remateProperties.getCorePoolSize())
                || isChanged(originalProperties.getMaximumPoolSize(), remateProperties.getMaximumPoolSize())
                || isChanged(originalProperties.getAllowCoreThreadTimeOut(), remateProperties.getAllowCoreThreadTimeOut())
                || isChanged(originalProperties.getKeepAliveTime(), remateProperties.getKeepAliveTime())
                || isChanged(originalProperties.getRejectedHandler(), remateProperties.getRejectedHandler());
    }

    private <T> boolean isChanged(T before, T after) {
        return after != null && !Objects.equals(before, after);
    }
}
