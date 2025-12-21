package com.artemis.elastictp.core.alarm;

import cn.hutool.core.date.DateUtil;
import com.artemis.elastictp.core.config.ApplicationProperties;
import com.artemis.elastictp.core.executor.ElasticTpExecutor;
import com.artemis.elastictp.core.executor.ElasticTpRegistry;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorHolder;
import com.artemis.elastictp.core.executor.ThreadPoolExecutorProperties;
import com.artemis.elastictp.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.artemis.elastictp.core.notification.service.NotifierDispatcher;
import com.artemis.elastictp.core.toolkit.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池运行状态报警检查器
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolAlarmChecker {

    private final NotifierDispatcher notifierDispatcher;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            1,
            ThreadFactoryBuilder.builder()
                    .namePrefix("scheduler_thread-pool_alarm_checker")
                    .build()
    );

    private final Map<String, Long> lastRejectCountMap = new ConcurrentHashMap<>();

    /**
     * 启动定时检查任务
     */
    public void start() {
        // 每10秒检查一次，初始延迟0秒
        scheduler.scheduleWithFixedDelay(this::checkAlarm, 0, 5, TimeUnit.SECONDS);
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
            if (holder.getExecutorProperties().getAlarm().getEnable()) {
                checkQueueUsage(holder);
                checkActiveRate(holder);
                checkRejectCount(holder);
            }
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
            sendAlarmMessage("Capacity", holder);
        }
    }

    /**
     * 检查线程活跃度（活跃线程数 / 最大线程数）
     */
    private void checkActiveRate(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties properties = holder.getExecutorProperties();

        int activeCount = executor.getActiveCount(); // API 有锁，避免高频率调用
        int maximumPoolSize = executor.getMaximumPoolSize();

        if (maximumPoolSize == 0) {
            return;
        }

        int activeRate = (int) Math.round((activeCount * 100.0) / maximumPoolSize);
        int threshold = properties.getAlarm().getActiveThreshold();

        if (activeRate >= threshold) {
            sendAlarmMessage("Activity", holder);
        }
    }

    /**
     * 检查拒绝策略执行次数
     */
    private void checkRejectCount(ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        String threadPoolId = holder.getThreadPoolId();

        // 只处理自定义线程池类型
        if (!(executor instanceof ElasticTpExecutor)) {
            return;
        }

        ElasticTpExecutor oneThreadExecutor = (ElasticTpExecutor) executor;
        long currentRejectCount = oneThreadExecutor.getRejectCount().get();
        long lastRejectCount = lastRejectCountMap.getOrDefault(threadPoolId, 0L);

        // 首次初始化或拒绝次数增加时触发
        if (currentRejectCount > lastRejectCount) {
            sendAlarmMessage("Reject", holder);
            // 更新最后记录值
            lastRejectCountMap.put(threadPoolId, currentRejectCount);
        }
    }
    @SneakyThrows
    private void sendAlarmMessage(String alarmType, ThreadPoolExecutorHolder holder) {
        ThreadPoolExecutor executor = holder.getExecutor();
        ThreadPoolExecutorProperties properties = holder.getExecutorProperties();
        BlockingQueue<?> queue = executor.getQueue();

        long rejectCount = -1L;
        if (executor instanceof ElasticTpExecutor) {
            rejectCount = ((ElasticTpExecutor) executor).getRejectCount().get();
        }

        int workQueueSize = queue.size(); // API 有锁，避免高频率调用
        int remainingCapacity = queue.remainingCapacity(); // API 有锁，避免高频率调用
        ThreadPoolAlarmNotifyDTO alarm = ThreadPoolAlarmNotifyDTO.builder()
                .applicationName(ApplicationProperties.getApplicationName())
                .activeProfile(ApplicationProperties.getActiveProfile())
                .identify(InetAddress.getLocalHost().getHostAddress())
                .alarmType(alarmType)
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
                .receives(properties.getNotify().getReceives())
                .currentTime(DateUtil.now())
                .interval(properties.getAlarm().getInterval())
                .build();
        notifierDispatcher.sendAlarmMessage(alarm);
    }
}