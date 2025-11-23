package com.artemis.elastictp.config.nacos.cloud.starter.refresher;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.artemis.elastictp.core.executor.support.BlockingQueueTypeEnum;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Nacos Cloud 版本刷新处理器
 */
@Slf4j
@RequiredArgsConstructor
public class NacosCloudRefresherHandler implements ApplicationRunner {

    private final BootstrapConfigProperties properties;
    private ConfigService configService;

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
                        log.info("Listen for changes in the Nacos configuration file: {}", refresherProperties);

                        // TODO 刷新动态线程池对象核心参数
                    }
                });

        log.info("Dynamic thread pool refresher, add nacos cloud listener success. data-id: {}, group: {}", nacosConfig.getDataId(), nacosConfig.getGroup());
    }
}
