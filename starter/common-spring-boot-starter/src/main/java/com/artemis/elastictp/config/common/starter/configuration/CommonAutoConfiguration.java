package com.artemis.elastictp.config.common.starter.configuration;

import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.configuration.ElasticTpBaseConfiguration;
import com.artemis.elastictp.spring.base.enable.MarkerConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

/**
 * 基于配置中心的公共自动装配配置
 */
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@Import(ElasticTpBaseConfiguration.class)
@AutoConfigureAfter(ElasticTpBaseConfiguration.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class CommonAutoConfiguration {
}
