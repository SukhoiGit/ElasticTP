package com.artemis.elastictp.spring.base;

import java.lang.annotation.*;

/**
 * 动态线程池注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicThreadPool {
}
