package com.artemis.elastictp.config.apollo.starter.configuration;

import com.artemis.elastictp.config.apollo.starter.refresher.ApolloRefresherHandler;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import com.artemis.elastictp.spring.base.configuration.ElasticTpBaseConfiguration;
import com.artemis.elastictp.spring.base.enable.MarkerConfiguration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Apollo 配置中心自动装配
 */
@Configurable
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@Import(ElasticTpBaseConfiguration.class)
@AutoConfigureAfter(ElasticTpBaseConfiguration.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class ApolloAutoConfiguration {

    @Bean
    public ApolloRefresherHandler apolloRefresherHandler(BootstrapConfigProperties properties) {
        return new ApolloRefresherHandler(properties);
    }
}
