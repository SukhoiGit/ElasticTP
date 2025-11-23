/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

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
