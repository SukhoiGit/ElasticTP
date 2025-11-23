package com.artemis.elastictp.core.executor;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池执行器持有者对象
 */
@Data
@RequiredArgsConstructor
public class ThreadPoolExecutorHolder {

    /**
     * 线程池唯一标识
     */
    private final String threadPoolId;

    /**
     * 线程池
     */
    private final ThreadPoolExecutor executor;

    /**
     * 线程池属性参数
     */
    private final ThreadPoolExecutorProperties executorProperties;
}
