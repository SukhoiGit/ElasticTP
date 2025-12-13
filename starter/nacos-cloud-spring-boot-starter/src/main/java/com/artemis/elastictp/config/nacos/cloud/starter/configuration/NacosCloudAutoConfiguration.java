package com.artemis.elastictp.config.nacos.cloud.starter.configuration;

import com.artemis.elastictp.config.nacos.cloud.starter.refresher.NacosCloudRefresherHandler;
import com.artemis.elastictp.core.notification.service.NotifierDispatcher;
import com.artemis.elastictp.core.config.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.enable.MarkerConfiguration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Nacos Cloud 版本自动装配
 */
@Configurable
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class NacosCloudAutoConfiguration {

    @Bean
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(BootstrapConfigProperties properties,
                                                                 NotifierDispatcher notifierDispatcher) {
        return new NacosCloudRefresherHandler(properties, notifierDispatcher);
    }
}