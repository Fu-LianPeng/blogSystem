package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.Message;
import com.nowcoder.community.Mapper.MessageMapper;
import com.nowcoder.community.Util.TrieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-08-12 17:55
 */
@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    TrieUtil trieUtil;

    public List<Message> findConversation(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public List<Message> findLettersTo(int toId,String conversationId,int offset,int limit){
        return messageMapper.selectLettersTo(toId,conversationId,offset,limit);
    }


    public int findLetterCount(String conversationId){
        return messageMapper.selectLettersCount(conversationId);
    }
    public int findUnreadCount(String conversationId,int userId){
        return messageMapper.selectUnreadCount(conversationId,userId);
    }

    public int addLetter(Message message){
        message.setContent(trieUtil.filterSensitiveWord(message.getContent()));
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        return messageMapper.insertLetter(message);
    }
    public int changeStatus(List<Integer> ids,int status){
        return messageMapper.updateStatus(ids,status);
    }

    public Message findLatestMessageSys(int userId,String topics){
        return messageMapper.selectLatestMessage(userId, topics);
    }

    public int findMessageSysNum(int userId,String topics){
        return messageMapper.selectMessageNum(userId,topics);
    }

    public int findUnReadMessageSysNum(int userId,String topics){
        return messageMapper.selectUnreadMessageNum(userId,topics);
    }

}
