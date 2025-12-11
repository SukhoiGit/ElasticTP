package com.artemis.elastictp.config.nacos.cloud.starter.configuration;

import com.artemis.elastictp.config.nacos.cloud.starter.refresher.NacosCloudRefresherHandler;
import com.artemis.elastictp.core.notification.service.DingTalkMessageService;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * Nacos Cloud 版本自动装配
 */
@Configurable
public class NacosCloudAutoConfiguration {

    @Bean
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(BootstrapConfigProperties properties,
                                                                 DingTalkMessageService messageService) {
        return new NacosCloudRefresherHandler(properties, messageService);
    }
}