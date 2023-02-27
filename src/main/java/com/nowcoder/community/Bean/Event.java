package com.nowcoder.community.Bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Bean-2022-08-31 16:16
 */
public class Event {

    private String topic;

    /**
     * 事件的触发者
     * */
    private int triggerId;

    /**
     * 事件发送给谁
     * */
    private int ToId;

    /**
     * 事件的相关实体类型
     * */
    private int aboutEntityType;


    /**
     * 事件的相关实体的ID
     * */
    private int aboutEntityId;

    /**
     * 其它数据：帖子ID
     * */
    private Map<String,Object> data=new HashMap<>();


    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public Event setTriggerId(int triggerId) {
        this.triggerId = triggerId;
        return this;
    }

    public int getToId() {
        return ToId;
    }

    public Event setToId(int toId) {
        ToId = toId;
        return this;
    }

    public int getAboutEntityType() {
        return aboutEntityType;
    }

    public Event setAboutEntityType(int aboutEntityType) {
        this.aboutEntityType = aboutEntityType;
        return this;
    }

    public int getAboutEntityId() {
        return aboutEntityId;
    }

    public Event setAboutEntityId(int aboutEntityId) {
        this.aboutEntityId = aboutEntityId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        data.put(key,value);
        return this;
    }
}
