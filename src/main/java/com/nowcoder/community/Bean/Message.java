package com.nowcoder.community.Bean;

import lombok.Data;

import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Bean-2022-08-11 23:09
 */
@Data
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;//'0-未读;1-已读;2-删除;',
    private Date createTime;
}
