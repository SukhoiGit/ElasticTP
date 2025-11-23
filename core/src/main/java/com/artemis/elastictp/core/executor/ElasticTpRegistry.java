package com.artemis.elastictp.core.executor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  动态线程池管理器，用于统一管理线程池实例
 */
public class ElasticTpRegistry {

    /**
     *  线程持有者缓存，key 为线程池唯一标识，value 为线程池包装类
     */
    private static final Map<String, ThreadPoolExecutorHolder> HOLDER_MAP = new ConcurrentHashMap<String, ThreadPoolExecutorHolder>();

    /**
     * 注册线程池到管理器
     * @param threadPoolId  线程池唯一标识
     * @param executor      线程池执行器实例
     * @param properties    线程池参数配置
     */
    public static void put(String threadPoolId, ThreadPoolExecutor executor, ThreadPoolExecutorProperties properties) {
        ThreadPoolExecutorHolder holder = new ThreadPoolExecutorHolder(threadPoolId, executor, properties);
        HOLDER_MAP.put(threadPoolId, holder);
    }

    /**
     * 根据线程池 ID 获取对应的线程池包装对象
     * @param threadPoolId  线程池唯一标识
     * @return      线程池包装对象
     */
    public static ThreadPoolExecutorHolder getHolder(String threadPoolId) {
        return Optional.ofNullable(HOLDER_MAP.get(threadPoolId))
                .orElseThrow(() -> new RuntimeException("No thread pool executor found for id: " + threadPoolId));
    }
}
