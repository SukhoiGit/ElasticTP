package com.artemis.elastictp.core.notification.service;

import com.artemis.elastictp.core.notification.dto.ThreadPoolConfigChangeDTO;

/**
 * 通知接口，用于发送线程池变更通知与运行时告警
 */
public interface NotifierService {

    /**
     * 发送线程池配置变更通知
     *
     * @param configChange 配置变更信息
     */
    void sendChangeMessage(ThreadPoolConfigChangeDTO configChange);
}
