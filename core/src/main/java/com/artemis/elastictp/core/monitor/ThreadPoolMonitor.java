package com.artemis.elastictp.core.monitor;

import com.alibaba.fastjson2.JSON;
import com.artemis.elastictp.core.executor.ElasticTpExecutor;
import com.artemis.elastictp.core.executor.ElasticTpRegistry;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorHolder;
import com.artemis.elastictp.core.toolkit.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 线程池运行时监控器
 */
@Slf4j
public class ThreadPoolMonitor {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            1,
            ThreadFactoryBuilder.builder()
                    .namePrefix("scheduler_thread-pool_monitor")
                    .build()
    );

    /**
     * 启动定时检查任务
     */
    public void start() {
        // 每30秒检查一次，初始延迟0秒
        scheduler.scheduleWithFixedDelay(this::logMonitor, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 停止报警检查
     */
    public void stop() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void logMonitor() {
        Collection<ThreadPoolExecutorHolder> holders = ElasticTpRegistry.getAllHolders();
        for (ThreadPoolExecutorHolder holder : holders) {
            ThreadPoolMonitorDTO monitorDTO = buildThreadPoolMonitorDTO(holder);
            log.info("[ThreadPool Monitor] {} | Content: {}", holder.getThreadPoolId(), JSON.toJSON(monitorDTO));
        }
    }

    @SneakyThrows
    private ThreadPoolMonitorDTO buildThreadPoolMonitorDTO(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        BlockingQueue<?> queue = executor.getQueue();

        long rejectCount = -1L;
        if (executor instanceof ElasticTpExecutor) {
            rejectCount = ((ElasticTpExecutor) executor).getRejectCount().get();
        }

        return ThreadPoolMonitorDTO.builder()
                .threadPoolId(holder.getThreadPoolId())
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .activePoolSize(executor.getActiveCount())  // API 有锁，避免高频率调用
                .currentPoolSize(executor.getPoolSize())  // API 有锁，避免高频率调用
                .completedTaskCount(executor.getCompletedTaskCount())  // API 有锁，避免高频率调用
                .largestPoolSize(executor.getLargestPoolSize())  // API 有锁，避免高频率调用
                .workQueueName(queue.getClass().getSimpleName())
                .workQueueCapacity(queue.remainingCapacity())
                .workQueueSize(queue.size())
                .rejectedHandlerName(executor.getRejectedExecutionHandler().toString())
                .rejectCount(rejectCount)
                .build();
    }
}

