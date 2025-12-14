package com.artemis.elastictp.core.executor.support;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 拒绝策略代理处理器
 *
 * <p>
 * 用于通过 JDK 动态代理包装 {@link RejectedExecutionHandler}，统计线程池被拒绝的次数
 * 当调用的是 {@code rejectedExecution} 方法时进行计数
 * </p>
 *
 * <p>
 * 示例用途：用于线程池拒绝报警、拒绝率分析等运行时动态监控
 * </p>
 */
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    private final Object target;
    private final String threadPoolId;
    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();

        // TODO 触发拒绝策略异常告警
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}

