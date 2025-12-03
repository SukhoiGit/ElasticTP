package com.artemis.elastictp.spring.base.configuration;

import com.artemis.elastictp.spring.base.support.ElasticTpBeanPostProcessor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * 动态线程池基础 Spring 配置类
 */
@Configurable
public class ElasticTpBaseConfiguration {

    @Bean
    public ElasticTpBeanPostProcessor oneThreadBeanPostProcessor() {
        return new ElasticTpBeanPostProcessor();
    }

}

