package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-08-11 23:12
 */
@Mapper
public interface MessageMapper {

    /**
     *
     * @param userId 当前登录用户的id
     * @return 返回与当前登录用户有关的会话的最新的那条消息的列表
     */
    public List<Message> selectConversations(int userId,int offset,int limit);

    /**
     *
     * @param userId 当前登录用户的id
     * @return 返回当前登录用户的会话数目
     */
    public Integer selectConversationCount(int userId);


    /**
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return 返回与当前会话相关的私信列表
     */
    public List<Message> selectLetters(String conversationId,int offset,int limit);


    /**
     *
     * @param conversationId
     * @param offset
     * @param limit
     * @return 返回与当前会话相关的私信列表
     */
    public List<Message> selectLettersTo(int toId,String conversationId,int offset,int limit);


    /**
     *
     * @param conversationId
     * @return 返回当前会话的私信数目
     */
    public Integer selectLettersCount(String conversationId);


    /**
     *
     * @param conversationId 不传入则查询当前登录用户的所有未读消息数，传入则查询当前会话的未读消息数
     * @param userId 传入当前登录用户的id
     * @return 未读消息数
     */
    public Integer selectUnreadCount(String conversationId,int userId);


    public Integer insertLetter(Message message);

    public Integer updateStatus(List<Integer> ids,int status);



    public Message selectLatestMessage(int userId,String topics);

    public int selectUnreadMessageNum(int userId,String topics);

    public int selectMessageNum(int userId,String topics);
}
