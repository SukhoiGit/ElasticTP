package com.artemis.elastictp.spring.base.configuration;

import com.artemis.elastictp.spring.base.support.ApplicationContextHolder;
import com.artemis.elastictp.spring.base.support.ElasticTpBeanPostProcessor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * 动态线程池基础 Spring 配置类
 */
@Configurable
@EnableConfigurationProperties(BootstrapConfigProperties.class)
public class ElasticTpBaseConfiguration {

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @DependsOn("applicationContextHolder")
    public ElasticTpBeanPostProcessor oneThreadBeanPostProcessor(BootstrapConfigProperties properties) {
        return new ElasticTpBeanPostProcessor(properties);
    }

}

