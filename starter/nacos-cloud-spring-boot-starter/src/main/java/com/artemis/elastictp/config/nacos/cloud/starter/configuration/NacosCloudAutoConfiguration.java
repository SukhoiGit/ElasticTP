package com.artemis.elastictp.config.nacos.cloud.starter.configuration;

import com.artemis.elastictp.config.nacos.cloud.starter.refresher.NacosCloudRefresherHandler;
import com.artemis.elastictp.core.notification.service.DingTalkMessageService;
import com.artemis.elastictp.core.notification.service.NotifierDispatcher;
import com.artemis.elastictp.spring.base.configuration.BootstrapConfigProperties;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * Nacos Cloud 版本自动装配
 * <p>
 * 作者：马丁
 * 加项目群：早加入就是优势！500人内部项目群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 * 开发时间：2025-04-24
 */
@Configurable
public class NacosCloudAutoConfiguration {

    @Bean
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(BootstrapConfigProperties properties,
                                                                 NotifierDispatcher notifierDispatcher) {
        return new NacosCloudRefresherHandler(properties, notifierDispatcher);
    }
}
