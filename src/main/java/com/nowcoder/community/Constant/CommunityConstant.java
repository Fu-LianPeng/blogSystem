package com.nowcoder.community.Constant;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Constant-2022-08-09 20:24
 */
public interface CommunityConstant {


    /**
     * 实体类型
     */
    int POSTTYPE=1;
    int COMMENTTYPE=2;
    int REPLYTYPE=3;
    int USERTYPE=4;
    /**
     * kafka topics 类型
     */

    String TOPIC_COMMENT="comment";
    String TOPIC_LIKE="like";
    String TOPIC_FOLLOW="follow";
    String TOPIC_PUBLISH="publish";

    /**
     *系统用户ID
     * */

    int SYSTEM_USER_ID = 1;
}
