package com.artemis.elastictp.core.executor;

import com.artemis.elastictp.core.executor.support.RejectedProxyInvocationHandler;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  增强的动态，报警，和受监控线程池 elasticTp
 */
public class ElasticTpExecutor extends ThreadPoolExecutor {

    /**
     *  线程池的唯一标识，用来动态变更参数等
     */
    @Getter
    private final String threadPoolId;

    /**
     * 线程池拒绝策略执行次数
     */
    @Getter
    private final AtomicLong rejectCount = new AtomicLong();

    /**
     * Creates a new {@code ExtensibleThreadPoolExecutor} with the given initial parameters.
     *
     * @param threadPoolId    thread-pool id
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @param handler         the handler to use when execution is blocked
     *                        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue} or {@code unit}
     *                                  or {@code threadFactory} or {@code handler} is null
     */
    public ElasticTpExecutor(
            @NonNull String threadPoolId,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            @NonNull TimeUnit unit,
            @NonNull BlockingQueue<Runnable> workQueue,
            @NonNull ThreadFactory threadFactory,
            @NonNull RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

        // 通过动态代理设置拒绝策略执行次数
        setRejectedExecutionHandler(handler);

        // 设置动态线程池扩展属性：线程池 ID 标识
        this.threadPoolId = threadPoolId;
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        RejectedExecutionHandler rejectedProxy = (RejectedExecutionHandler) Proxy
                .newProxyInstance(
                        handler.getClass().getClassLoader(),
                        new Class[]{RejectedExecutionHandler.class},
                        new RejectedProxyInvocationHandler(handler, rejectCount)
                );
        setRejectedExecutionHandler(rejectedProxy);
        super.setRejectedExecutionHandler(rejectedProxy);
    }
}

