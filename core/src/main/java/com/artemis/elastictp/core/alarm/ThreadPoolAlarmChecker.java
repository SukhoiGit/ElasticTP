package com.artemis.elastictp.core.alarm;

import com.artemis.elastictp.core.executor.ElasticTpRegistry;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorHolder;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorProperties;
import com.artemis.elastictp.core.toolkit.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 线程池运行状态报警检查器
 */
@Slf4j
public class ThreadPoolAlarmChecker {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            1,
            ThreadFactoryBuilder.builder()
                    .namePrefix("scheduler_thread-pool_alarm_checker")
                    .build()
    );

    /**
     * 启动定时检查任务
     */
    public void start() {
        // 每10秒检查一次，初始延迟0秒
        scheduler.scheduleAtFixedRate(this::checkAlarm, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * 停止报警检查
     */
    public void stop() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * 报警检查核心逻辑
     */
    private void checkAlarm() {
        Collection<ThreadPoolExecutorHolder> holders = ElasticTpRegistry.getAllHolders();
        for (ThreadPoolExecutorHolder holder : holders) {
            checkQueueUsage(holder);
            checkActiveRate(holder);
        }
    }

    /**
     * 检查队列使用率
     */
    private void checkQueueUsage(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties properties = holder.getExecutorProperties();

        BlockingQueue<?> queue = executor.getQueue();
        int queueSize = queue.size();
        int capacity = queueSize + queue.remainingCapacity();

        if (capacity == 0) {
            return;
        }

        int usageRate = (int) Math.round((queueSize * 100.0) / capacity);
        int threshold = properties.getAlarm().getQueueThreshold();

        if (usageRate >= threshold) {
            log.warn("[队列报警] 线程池ID={}，队列使用率={}%，当前队列大小={}，容量={}",
                    holder.getThreadPoolId(),
                    usageRate,
                    queueSize,
                    capacity
            );
            // TODO 报警通知
        }
    }


    /**
     * 检查线程活跃度（活跃线程数 / 最大线程数）
     */
    private void checkActiveRate(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties properties = holder.getExecutorProperties();

        int activeCount = executor.getActiveCount(); // API 有锁，已避免重复调用
        int maximumPoolSize = executor.getMaximumPoolSize();

        if (maximumPoolSize == 0) {
            return;
        }

        int activeRate = (int) Math.round((activeCount * 100.0) / maximumPoolSize);
        int threshold = properties.getAlarm().getActiveThreshold();

        if (activeRate >= threshold) {
            log.warn("[活跃线程报警] 线程池ID={}，活跃线程数={}，最大线程数={}，活跃率={}%",
                    holder.getThreadPoolId(),
                    activeCount,
                    maximumPoolSize,
                    activeRate
            );
            // TODO 报警通知
        }
    }
}
