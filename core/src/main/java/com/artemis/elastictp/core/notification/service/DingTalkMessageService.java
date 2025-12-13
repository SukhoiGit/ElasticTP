package com.artemis.elastictp.core.notification.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.artemis.elastictp.core.notification.dto.ThreadPoolConfigChangeDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.artemis.elastictp.core.constant.Constants.DING_CONFIG_CHANGE_MESSAGE_TEXT;

/**
 * 钉钉消息通知服务
 */
@Slf4j
public class DingTalkMessageService implements NotifierService{

    /**
     * 发送线程池配置变更通知到钉钉机器人
     *
     * @param configChangeDTO 线程池配置变更数据传输对象，包含变更详情和接收人信息
     */
    @Override
    public void sendChangeMessage(ThreadPoolConfigChangeDTO configChangeDTO) {
        Map<String, Object> markdown = getStringObjectHashMap(configChangeDTO);
        Map<String, Object> at = new HashMap<>();
        at.put("atMobiles", CollectionUtil.newArrayList(configChangeDTO.getReceives().split(",")));
        Map<String, Object> dingTaskMarkdownRequest = new HashMap<>();
        dingTaskMarkdownRequest.put("msgtype", "markdown");
        dingTaskMarkdownRequest.put("markdown", markdown);
        dingTaskMarkdownRequest.put("at", at);
        try {
            String serverUrl = configChangeDTO.getNotifyPlatforms().getUrl();
            String responseBody = HttpUtil.post(serverUrl, JSON.toJSONString(dingTaskMarkdownRequest));
            DingTalkMessageService.DingRobotResponse response = JSON.parseObject(responseBody, DingTalkMessageService.DingRobotResponse.class);
            if (response.getErrcode() != 0) {
                log.error("Ding failed to send message, reason: {}", response.errmsg);
            }
        } catch (Exception ex) {
            log.error("Ding failed to send message.", ex);
        }
    }

    private static Map<String, Object> getStringObjectHashMap(ThreadPoolConfigChangeDTO configChangeDTO) {
        Map<String, ThreadPoolConfigChangeDTO.ChangePair<?>> changes = configChangeDTO.getChanges();
        String markdownText = String.format(
                DING_CONFIG_CHANGE_MESSAGE_TEXT,
                configChangeDTO.getActive().toUpperCase(),
                configChangeDTO.getThreadPoolId(),
                configChangeDTO.getIdentify() + ":" + configChangeDTO.getApplicationName(),
                changes.get("corePoolSize").getBefore() + " ➲ " + changes.get("corePoolSize").getAfter(),
                changes.get("maximumPoolSize").getBefore() + " ➲ " + changes.get("maximumPoolSize").getAfter(),
                changes.get("keepAliveTime").getBefore() + " ➲ " + changes.get("keepAliveTime").getAfter(),
                configChangeDTO.getWorkQueue(),
                changes.get("queueCapacity").getBefore() + " ➲ " + changes.get("queueCapacity").getAfter(),
                changes.get("rejectedHandler").getBefore(),
                changes.get("rejectedHandler").getAfter(),
                configChangeDTO.getReceives(),
                configChangeDTO.getUpdateTime()
        );
        HashMap<String, Object> markdown = new HashMap<>();
        markdown.put("title", "动态线程池通知");
        markdown.put("text", markdownText);
        return markdown;
    }

    @Data
    static class DingRobotResponse {

        private Long errcode;
        private String errmsg;
    }
}
