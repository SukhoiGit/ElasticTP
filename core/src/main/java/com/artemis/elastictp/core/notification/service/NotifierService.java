package com.artemis.elastictp.core.notification.service;

import com.artemis.elastictp.core.notification.dto.ThreadPoolConfigChangeDTO;

/**
 * 通知接口，用于发送线程池变更通知与运行时告警
 * <p>
 * 作者：马丁
 * 加项目群：早加入就是优势！500人内部项目群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 * 开发时间：2025-05-03
 */
public interface NotifierService {

    /**
     * 发送线程池配置变更通知
     *
     * @param configChange 配置变更信息
     */
    void sendChangeMessage(ThreadPoolConfigChangeDTO configChange);
}
