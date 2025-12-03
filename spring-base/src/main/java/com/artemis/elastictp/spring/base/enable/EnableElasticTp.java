package com.artemis.elastictp.spring.base.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 动态启用 oneThread 动态线程池开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MarkerConfiguration.class)
public @interface EnableElasticTp {
}

