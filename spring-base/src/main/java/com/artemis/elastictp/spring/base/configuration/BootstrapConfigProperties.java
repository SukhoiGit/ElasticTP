package com.artemis.elastictp.spring.base.configuration;

import com.artemis.elastictp.core.executor.ThreadPoolExecutorProperties;
import com.artemis.elastictp.spring.base.parser.ConfigFileTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * elasticTp 配置中心参数
 */
@Data
@ConfigurationProperties(prefix = BootstrapConfigProperties.PREFIX)
public class BootstrapConfigProperties {
    public static final String PREFIX = "elastictp";

    /**
     * 是否开启动态线程池开关
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Nacos 配置文件
     */
    private NacosConfig nacos;

    /**
     * Apollo 配置文件
     */
    private ApolloConfig apollo;

    /**
     * Nacos 远程配置文件格式类型
     */
    private ConfigFileTypeEnum configFileType;

    /**
     * 通知配置
     */
    private NotifierConfig notifiers;

    /**
     * 线程池配置集合
     */
    private List<ThreadPoolExecutorProperties> executors;

    @Data
    public static class NotifierConfig {

        /**
         * 完整 WebHook 地址
         */
        private String url;
    }

    @Data
    public static class NacosConfig {

        private String dataId;

        private String group;
    }

    @Data
    public static class ApolloConfig {

        private String namespace;
    }

}

