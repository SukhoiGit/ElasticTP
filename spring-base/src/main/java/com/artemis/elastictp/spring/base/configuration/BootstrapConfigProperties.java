package com.artemis.elastictp.spring.base.configuration;

import com.artemis.elastictp.core.executor.ThreadPoolExecutorProperties;
import com.artemis.elastictp.spring.base.parser.ConfigFileTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * oneThread 配置中心参数
 * <p>
 * 作者：马丁
 * 加项目群：早加入就是优势！500人内部项目群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 * 开发时间：2025-04-23
 */
@Data
@ConfigurationProperties(prefix = BootstrapConfigProperties.PREFIX)
public class BootstrapConfigProperties {
    public static final String PREFIX = "onethread";

    /**
     * 是否开启动态线程池开关
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Nacos 配置文件
     */
    private NacosConfig nacos;

    /**
     * Nacos 远程配置文件格式类型
     */
    private ConfigFileTypeEnum configFileType;

    /**
     * 线程池配置集合
     */
    private List<ThreadPoolExecutorProperties> executors;

    @Data
    public static class NacosConfig {

        private String dataId;

        private String group;
    }

}
