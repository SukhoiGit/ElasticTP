package com.artemis.elastictp.config.common.starter.configuration;

import com.artemis.elastictp.core.config.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.configuration.ElasticTpBaseConfiguration;
import com.artemis.elastictp.spring.base.enable.MarkerConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * 基于配置中心的公共自动装配配置
 */
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@Import(ElasticTpBaseConfiguration.class)
@AutoConfigureAfter(ElasticTpBaseConfiguration.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class CommonAutoConfiguration {
    @Bean
    public BootstrapConfigProperties bootstrapConfigProperties(Environment environment) {
        BootstrapConfigProperties bootstrapConfigProperties = Binder.get(environment)
                .bind(BootstrapConfigProperties.PREFIX, Bindable.of(BootstrapConfigProperties.class))
                .get();
        BootstrapConfigProperties.setInstance(bootstrapConfigProperties);
        return bootstrapConfigProperties;
    }

    @Bean
    public ElasticTpBannerHandler elasticTpBannerHandler(ObjectProvider<BuildProperties> buildProperties) {
        return new ElasticTpBannerHandler(buildProperties.getIfAvailable());
    }
}
