package com.nowcoder.community.Util;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.lang.reflect.ParameterizedType;
import java.util.Spliterator;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-22 23:21
 */
public class RedisKeyUtil {

    public static final String SPLIT = ":";
    public static final String PREFIX_ENTITY_LIKE = "like:entity";
    public static final String PREFIX_USER_LIKE = "like:user";
    public static final String PREFIX_FOLLOWER = "follower";//实体的关注列表
    public static final String PREFIX_FOLLOWEE = "followee";//用户的关注列表

    public static final String PREFIX_KAPTCHA = "kaptcha";


    public static final String PERFIX_TICKET = "ticket";
    public static final String PERFIX_USER = "user";
    public static final String PEFIX_SCORE = "score";


    public static String getHotPost(int offset) {
        return "hotpost" + SPLIT + offset;
    }

    public static String getPostRows() {
        return "postRows";
    }
    public static String getPostRows(int postId) {
        return "postRows"+SPLIT+postId;
    }

    public static String getPostScore() {
        return PEFIX_SCORE + SPLIT + "post";
    }

    public static String getLoginUserKey(int userId) {
        return PERFIX_USER + SPLIT + userId;
    }

    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }


    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getFolloweeKey(int entityType, int userId) {
        return PREFIX_FOLLOWEE + SPLIT + entityType + SPLIT + userId;
    }

    public static String getKaptchaKey(String Owner) {
        return PREFIX_KAPTCHA + SPLIT + Owner;
    }


    public static String getTicketKey(String ticket) {
        return PERFIX_TICKET + SPLIT + ticket;
    }
}
