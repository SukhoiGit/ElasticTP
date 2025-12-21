package com.artemis.elastictp.spring.base.configuration;

import com.artemis.elastictp.core.alarm.ThreadPoolAlarmChecker;
import com.artemis.elastictp.core.config.BootstrapConfigProperties;
import com.artemis.elastictp.core.monitor.ThreadPoolMonitor;
import com.artemis.elastictp.core.notification.service.NotifierDispatcher;
import com.artemis.elastictp.spring.base.support.ApplicationContextHolder;
import com.artemis.elastictp.spring.base.support.ElasticTpBeanPostProcessor;
import com.artemis.elastictp.spring.base.support.SpringPropertiesLoader;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * 动态线程池基础 Spring 配置类
 */
@Configurable
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

    @Bean
    public NotifierDispatcher notifierDispatcher() {
        return new NotifierDispatcher();
    }

    @Bean
    public SpringPropertiesLoader springPropertiesLoader() {
        return new SpringPropertiesLoader();
    }
    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolAlarmChecker threadPoolAlarmChecker(NotifierDispatcher notifierDispatcher) {
        return new ThreadPoolAlarmChecker(notifierDispatcher);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolMonitor threadPoolMonitor() {
        return new ThreadPoolMonitor();
    }

}

