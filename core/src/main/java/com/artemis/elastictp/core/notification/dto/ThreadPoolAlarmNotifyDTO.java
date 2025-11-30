package com.artemis.elastictp.core.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线程池运行时告警通知实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolAlarmNotifyDTO {

    /**
     * 线程池唯一标识
     */
    private String threadPoolId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 环境标识
     */
    private String activeProfile;

    /**
     * 应用节点唯一标识
     */
    private String identify;

    /**
     * 通知接收人
     */
    private String receives;

    /**
     * 报警类型：Capacity、Activity、Reject
     */
    private String alarmType;

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;

    /**
     * 当前线程数
     */
    private Integer currentPoolSize;

    /**
     * 活跃线程数
     */
    private Integer activePoolSize;

    /**
     * 最大线程数
     */
    private Integer largestPoolSize;

    /**
     * 线程池任务总量
     */
    private Long completedTaskCount;

    /**
     * 阻塞队列类型
     */
    private String workQueueName;

    /**
     * 队列容量
     */
    private Integer workQueueCapacity;

    /**
     * 队列元素数量
     */
    private Integer workQueueSize;

    /**
     * 拒绝策略
     */
    private String rejectedHandlerName;

    /**
     * 执行拒绝策略次数
     */
    private Long rejectCount;

    /**
     * 当前时间
     */
    private String currentTime;

    /**
     * 报警间隔，单位分钟
     */
    private Integer interval;
}

