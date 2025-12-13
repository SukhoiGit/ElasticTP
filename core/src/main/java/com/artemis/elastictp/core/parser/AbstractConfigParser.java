package com.artemis.elastictp.core.parser;

/**
 * 配置解析器抽象类
 */
public abstract class AbstractConfigParser implements ConfigParser {

    @Override
    public boolean supports(ConfigFileTypeEnum type) {
        return getConfigFileTypes().contains(type);
    }
}

