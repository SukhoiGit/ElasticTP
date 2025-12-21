package com.artemis.elastictp.core.monitor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.artemis.elastictp.core.config.ApplicationProperties;
import com.artemis.elastictp.core.config.BootstrapConfigProperties;
import com.artemis.elastictp.core.executor.ElasticTpExecutor;
import com.artemis.elastictp.core.executor.ElasticTpRegistry;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorHolder;
import com.artemis.elastictp.core.toolkit.ThreadFactoryBuilder;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 线程池运行时监控器
 */
@Slf4j
public class ThreadPoolMonitor {

    private ScheduledExecutorService scheduler;
    private Map<String, ThreadPoolMonitorDTO> micrometerMonitorCache;

    private static final String METRIC_NAME_PREFIX = "dynamic.thread-pool";
    private static final String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";
    private static final String APPLICATION_NAME_TAG = "application.name";

    /**
     * 启动定时检查任务
     */
    public void start() {
        BootstrapConfigProperties.MonitorConfig monitorConfig = BootstrapConfigProperties.getInstance().getMonitorConfig();
        if (!monitorConfig.getEnable()) {
            return;
        }

        // 初始化监控相关资源
        micrometerMonitorCache = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(
                1,
                ThreadFactoryBuilder.builder()
                        .namePrefix("scheduler_thread-pool_monitor")
                        .build()
        );

        // 每指定时间检查一次，初始延迟0秒
        scheduler.scheduleWithFixedDelay(() -> {
            Collection<ThreadPoolExecutorHolder> holders = ElasticTpRegistry.getAllHolders();
            for (ThreadPoolExecutorHolder holder : holders) {
                ThreadPoolMonitorDTO monitorDTO = buildThreadPoolMonitorDTO(holder);

                // 根据采集类型判断
                if (Objects.equals(monitorConfig.getCollectType(), "log")) {
                    logMonitor(monitorDTO);
                } else if (Objects.equals(monitorConfig.getCollectType(), "micrometer")) {
                    micrometerMonitor(monitorDTO);
                }
            }
        }, 0, monitorConfig.getCollectInterval(), TimeUnit.SECONDS);
    }

    /**
     * 停止报警检查
     */
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void logMonitor(ThreadPoolMonitorDTO monitorDTO) {
        log.info("[ThreadPool Monitor] {} | Content: {}", monitorDTO.getThreadPoolId(), JSON.toJSON(monitorDTO));
    }

    private void micrometerMonitor(ThreadPoolMonitorDTO monitorDTO) {
        String threadPoolId = monitorDTO.getThreadPoolId();
        ThreadPoolMonitorDTO existingDTO = micrometerMonitorCache.get(threadPoolId);
        if (existingDTO != null) {
            BeanUtil.copyProperties(monitorDTO, existingDTO);
        } else {
            micrometerMonitorCache.put(threadPoolId, monitorDTO);
            existingDTO = monitorDTO;
        }

        Iterable<Tag> tags = CollectionUtil.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, threadPoolId),
                Tag.of(APPLICATION_NAME_TAG, ApplicationProperties.getApplicationName()));

        Metrics.gauge(metricName("core.size"), tags, existingDTO, ThreadPoolMonitorDTO::getCorePoolSize);
        Metrics.gauge(metricName("maximum.size"), tags, existingDTO, ThreadPoolMonitorDTO::getMaximumPoolSize);
        Metrics.gauge(metricName("current.size"), tags, monitorDTO, ThreadPoolMonitorDTO::getActivePoolSize);
        Metrics.gauge(metricName("largest.size"), tags, monitorDTO, ThreadPoolMonitorDTO::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, monitorDTO, ThreadPoolMonitorDTO::getActivePoolSize);
        Metrics.gauge(metricName("queue.size"), tags, monitorDTO, ThreadPoolMonitorDTO::getWorkQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, monitorDTO, ThreadPoolMonitorDTO::getWorkQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, monitorDTO, ThreadPoolMonitorDTO::getWorkQueueRemainingCapacity);
        Metrics.gauge(metricName("completed.task.count"), tags, monitorDTO, ThreadPoolMonitorDTO::getCompletedTaskCount);
        Metrics.gauge(metricName("reject.count"), tags, monitorDTO, ThreadPoolMonitorDTO::getRejectCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @SneakyThrows
    private ThreadPoolMonitorDTO buildThreadPoolMonitorDTO(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        BlockingQueue<?> queue = executor.getQueue();

        long rejectCount = -1L;
        if (executor instanceof ElasticTpExecutor) {
            rejectCount = ((ElasticTpExecutor) executor).getRejectCount().get();
        }

        int workQueueSize = queue.size(); // API 有锁，避免高频率调用
        int remainingCapacity = queue.remainingCapacity(); // API 有锁，避免高频率调用
        return ThreadPoolMonitorDTO.builder()
                .threadPoolId(holder.getThreadPoolId())
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .activePoolSize(executor.getActiveCount())  // API 有锁，避免高频率调用
                .currentPoolSize(executor.getPoolSize())  // API 有锁，避免高频率调用
                .completedTaskCount(executor.getCompletedTaskCount())  // API 有锁，避免高频率调用
                .largestPoolSize(executor.getLargestPoolSize())  // API 有锁，避免高频率调用
                .workQueueName(queue.getClass().getSimpleName())
                .workQueueSize(workQueueSize)
                .workQueueRemainingCapacity(remainingCapacity)
                .workQueueCapacity(workQueueSize + remainingCapacity)
                .rejectedHandlerName(executor.getRejectedExecutionHandler().toString())
                .rejectCount(rejectCount)
                .build();
    }
}

