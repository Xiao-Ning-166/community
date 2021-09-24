package edu.hue.community.event;

import com.alibaba.fastjson.JSONObject;
import edu.hue.community.entity.Event;
import edu.hue.community.entity.Message;
import edu.hue.community.service.MessageService;
import edu.hue.community.util.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/22
 * 事件消费者类
 */
@Component
@Slf4j
public class EventConsumer {

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {MessageConstant.TOPIC_COMMENT, MessageConstant.TOPIC_LIKE, MessageConstant.TOPIC_FOLLOW})
    public void handleEvent(ConsumerRecord consumerRecord) {
        if (consumerRecord == null || consumerRecord.value() == null) {
            log.error("消息的内容为空！！！");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(), Event.class);
        if (event == null) {
            log.error("消息的格式不正确！！！");
            return;
        }
        Message message = new Message();
        message.setFromId(MessageConstant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.insertMessage(message);
    }

}
