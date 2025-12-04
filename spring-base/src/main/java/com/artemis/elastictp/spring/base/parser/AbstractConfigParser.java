package com.artemis.elastictp.spring.base.parser;

/**
 * 配置解析器抽象类
 */
public abstract class AbstractConfigParser implements ConfigParser {

    @Override
    public boolean supports(ConfigFileTypeEnum type) {
        return getConfigFileTypes().contains(type);
    }
}

