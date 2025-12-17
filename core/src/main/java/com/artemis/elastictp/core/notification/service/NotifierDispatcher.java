package com.artemis.elastictp.core.notification.service;

import com.artemis.elastictp.core.config.BootstrapConfigProperties;
import com.artemis.elastictp.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.artemis.elastictp.core.notification.dto.ThreadPoolConfigChangeDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 通知调度器，用于统一管理和路由各类通知发送器（如钉钉、飞书、企业微信等）
 * <p>
 * 该类屏蔽了具体通知平台的实现细节，对上层调用者提供统一的通知发送入口
 * 内部根据配置自动初始化可用的 Notifier 实现，并在发送通知时根据平台类型动态路由到对应的发送器
 * <p>
 */
public class NotifierDispatcher implements NotifierService {

    private static final Map<String, NotifierService> NOTIFIER_SERVICE_MAP = new HashMap<>();

    static {
        NOTIFIER_SERVICE_MAP.put("DING", new DingTalkMessageService());
    }

    @Override
    public void sendChangeMessage(ThreadPoolConfigChangeDTO configChange) {
        Optional<NotifierService> notifierService = Optional.ofNullable(BootstrapConfigProperties.getInstance().getNotifyPlatforms())
                .map(BootstrapConfigProperties.NotifyPlatformsConfig::getPlatform)
                .map(each -> NOTIFIER_SERVICE_MAP.get(each));
        if (notifierService.isPresent()) {
            notifierService.get().sendChangeMessage(configChange);
        }
    }

    @Override
    public void sendAlarmMessage(ThreadPoolAlarmNotifyDTO alarm) {
        Optional<NotifierService> notifierService = Optional.ofNullable(BootstrapConfigProperties.getInstance().getNotifyPlatforms())
                .map(BootstrapConfigProperties.NotifyPlatformsConfig::getPlatform)
                .map(each -> NOTIFIER_SERVICE_MAP.get(each));
        if (notifierService.isPresent()) {
            // 频率检查
            boolean allowSend = AlarmRateLimiter.allowAlarm(
                    alarm.getThreadPoolId(),
                    alarm.getAlarmType(),
                    alarm.getInterval()
            );

            // 满足频率发送告警
            if (allowSend) {
                notifierService.get().sendAlarmMessage(alarm);
            }
        }
    }
}