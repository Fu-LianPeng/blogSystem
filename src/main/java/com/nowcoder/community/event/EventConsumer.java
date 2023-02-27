package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Bean.Event;
import com.nowcoder.community.Bean.Message;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.DiscussPostService;
import com.nowcoder.community.Service.ElasticsearchService;
import com.nowcoder.community.Service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author fulianpeng
 * @create com.nowcoder.community.event-2022-08-31 21:48
 */
@Component
public class EventConsumer {

    Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    MessageService messageService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    ElasticsearchService elasticsearchService;


    @KafkaListener(topics = {CommunityConstant.TOPIC_COMMENT, CommunityConstant.TOPIC_FOLLOW, CommunityConstant.TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord consumerRecord) {

        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息内容为空");
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(), Event.class);

        Message message = new Message();

        message.setFromId(CommunityConstant.SYSTEM_USER_ID);
        message.setToId(event.getToId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", event.getTriggerId());
        map.put("entityType", event.getAboutEntityType());
        map.put("entityId", event.getAboutEntityId());


        if (event.getData().size() != 0) {
            for (Map.Entry<String, Object> data : event.getData().entrySet()) {
                map.put(data.getKey(), data.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));

        messageService.addLetter(message);
    }


    @KafkaListener(topics = {CommunityConstant.TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord consumerRecord) {
        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息内容为空");
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(), Event.class);
        DiscussPost post = JSONObject.parseObject(event.getData().get("post").toString(),DiscussPost.class);
        elasticsearchService.saveDiscussPost(post);
    }
}
