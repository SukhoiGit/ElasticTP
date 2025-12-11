package com.artemis.elastictp.config.nacos.cloud.starter.refresher;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.artemis.elastictp.config.common.starter.refresher.AbstractDynamicThreadPoolRefresher;
import com.artemis.elastictp.core.executor.support.BlockingQueueTypeEnum;
import com.artemis.elastictp.core.notification.service.DingTalkMessageService;
import com.artemis.elastictp.core.toolkit.ThreadPoolExecutorBuilder;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.support.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;

/**
 * Nacos Cloud 版本刷新处理器
 */
@Slf4j(topic = "ElasticTpConfigRefresher")
public class NacosCloudRefresherHandler extends AbstractDynamicThreadPoolRefresher {

    private ConfigService configService;

    public NacosCloudRefresherHandler(BootstrapConfigProperties properties, DingTalkMessageService messageService) {
        super(properties, messageService);
        configService = ApplicationContextHolder.getBean(NacosConfigProperties.class).configServiceInstance();
    }

    public void registerListener() throws NacosException {
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

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        refreshThreadPoolProperties(configInfo);
                    }
                });

        log.info("Dynamic thread pool refresher, add nacos cloud listener success. data-id: {}, group: {}", nacosConfig.getDataId(), nacosConfig.getGroup());
    }
}
