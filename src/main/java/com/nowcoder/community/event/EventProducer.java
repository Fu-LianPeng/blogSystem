package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.Bean.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Target;
import java.util.Scanner;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.event-2022-08-31 16:36
 */
@Component
public class EventProducer {

    @Autowired
    KafkaTemplate<String,Object> kafkaTemplate;

    public void fireEvent(Event event){

        kafkaTemplate.send(event.getTopic(), JSONObject.toJSON(event).toString());
    }

}
