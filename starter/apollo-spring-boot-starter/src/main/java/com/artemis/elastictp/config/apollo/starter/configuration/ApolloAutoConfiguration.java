package com.artemis.elastictp.config.apollo.starter.configuration;

import com.artemis.elastictp.config.apollo.starter.refresher.ApolloRefresherHandler;
import com.artemis.elastictp.core.notification.service.DingTalkMessageService;
import com.artemis.elastictp.core.notification.service.NotifierDispatcher;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * Apollo 配置中心自动装配
 */
@Configurable
public class ApolloAutoConfiguration {

    @Bean
    public ApolloRefresherHandler apolloRefresherHandler(BootstrapConfigProperties properties,
                                                         NotifierDispatcher notifierDispatcher) {
        return new ApolloRefresherHandler(properties, notifierDispatcher);
    }

}
