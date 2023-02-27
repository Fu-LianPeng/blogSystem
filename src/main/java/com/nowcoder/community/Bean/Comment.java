package com.nowcoder.community.Bean;

import lombok.Data;

import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Bean-2022-08-09 19:50
 */
@Data
public class Comment {

    /*`id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `entity_type` int(11) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  `target_id` int(11) DEFAULT NULL,
  `content` text,
  `status` int(11) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,*/
    int id;
    int userId;
    int entityType;
    int entityId;
    int targetId;
    String content;
    int status;
    Date createTime;
}
