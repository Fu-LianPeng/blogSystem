package com.nowcoder.community.Controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.Bean.Message;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.MessageService;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.MyPageHelper;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.annotation.MustLoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-12 18:00
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;
    @Autowired
    public HostHolder hostHolder;

    @MustLoginPage
    @RequestMapping(value = "/letter/list", method = RequestMethod.GET)
    public String getConversationList(Model model, MyPageHelper myPageHelper) {
        User user = hostHolder.getUser();
        myPageHelper.setTotalRow(messageService.findConversationCount(user.getId()));
        myPageHelper.setUrl("/letter/list");


        List<Message> letterList = messageService.findConversation(user.getId(), myPageHelper.getOffset(), myPageHelper.getLimit());

        List<Map<String, Object>> letterUserList = new ArrayList<>();
        for (Message letter : letterList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("letter", letter);
            map.put("unReadCount", messageService.findUnreadCount(letter.getConversationId(), user.getId()));
            map.put("TheOtherUser", userService.getUser(user.getId() == letter.getFromId() ? letter.getToId() : letter.getFromId()));
            map.put("letterCount", messageService.findLetterCount(letter.getConversationId()));
            letterUserList.add(map);
        }
        model.addAttribute("letterUserList", letterUserList);
        model.addAttribute("totUnReadCount", messageService.findUnreadCount(null, user.getId()));
        model.addAttribute("SysunReadNum", messageService.findUnReadMessageSysNum(user.getId(), null));
        return "/site/letter";
    }

    @MustLoginPage
    @RequestMapping(value = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, MyPageHelper pageHelper) {
        pageHelper.setLimit(5);
        pageHelper.setTotalRow(messageService.findLetterCount(conversationId));
        pageHelper.setUrl("/letter/detail/" + conversationId);
        User user = hostHolder.getUser();
        List<Message> letters = messageService.findLetters(conversationId, pageHelper.getOffset(), pageHelper.getLimit());
        List<Map<String, Object>> letterUserLists = new ArrayList<>();
        List<Integer> idsUnread = new ArrayList<>();//用于更改已读状态的
        for (int i = letters.size() - 1; i >= 0; i--) {
            Message letter = letters.get(i);
            HashMap<String, Object> map = new HashMap<>();
            map.put("letter", letter);
            map.put("mySendUser", user.getId() == letter.getFromId() ? userService.getUser(letter.getFromId()) : null);
            map.put("myRecvUser", user.getId() == letter.getFromId() ? null : userService.getUser(letter.getFromId()));
            letterUserLists.add(map);
            if (letter.getToId() == user.getId() && letter.getStatus() == 0)
                idsUnread.add(letter.getId());
        }
        String[] split = conversationId.split("_");
        String id = split[0].equals(user.getId() + "") ? split[1] : split[0];
        model.addAttribute("theOtherUser", userService.getUser(Integer.parseInt(id)));
        model.addAttribute("letterUserLists", letterUserLists);
        if (idsUnread.size() != 0)
            messageService.changeStatus(idsUnread, 1);
        return "/site/letter-detail";
    }

    @ResponseBody
    @RequestMapping(value = "/letter/send", method = RequestMethod.POST)
    public String sendLetter(String toName, String content) {
        User toUser = userService.getUser(toName);
        if (toUser == null)
            return StringUtils.getJsonString(204, "发送用户不存在");
        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(toUser.getId());
        String conversationId;
        if (toUser.getId() > hostHolder.getUser().getId()) {
            conversationId = hostHolder.getUser().getId() + "_" + toUser.getId();
        } else {
            conversationId = toUser.getId() + "_" + hostHolder.getUser().getId();
        }
        message.setConversationId(conversationId);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addLetter(message);
        return StringUtils.getJsonString(0, "发送成功");
    }

    @MustLoginPage
    @RequestMapping(value = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        Message latestComment = messageService.findLatestMessageSys(user.getId(), CommunityConstant.TOPIC_COMMENT);
        Message latestLike = messageService.findLatestMessageSys(user.getId(), CommunityConstant.TOPIC_LIKE);
        Message latestFollow = messageService.findLatestMessageSys(user.getId(), CommunityConstant.TOPIC_FOLLOW);

        HashMap<String, Object> map;
        if (latestComment != null) {
            map = new HashMap<>();
            Map data = JSONObject.parseObject(HtmlUtils.htmlUnescape(latestComment.getContent()), Map.class);
            map.put("latestComment", latestComment);
            map.put("entityType", data.get("entityType"));
            map.put("postId", data.get("postId"));
            map.put("triggerId", data.get("userId"));
            map.put("trigger", userService.getUser((Integer) data.get("userId")));
            map.put("unReadNum", messageService.findUnReadMessageSysNum(user.getId(), CommunityConstant.TOPIC_COMMENT));
            map.put("num", messageService.findMessageSysNum(user.getId(), CommunityConstant.TOPIC_COMMENT));
            model.addAttribute("comment", map);
        }
        if (latestLike != null) {
            map = new HashMap<>();
            Map data = JSONObject.parseObject(HtmlUtils.htmlUnescape(latestLike.getContent()), Map.class);
            map.put("latestLike", latestLike);
            map.put("entityType", data.get("entityType"));
            map.put("postId", data.get("postId"));
            map.put("triggerId", data.get("userId"));
            map.put("trigger", userService.getUser((Integer) data.get("userId")));
            map.put("unReadNum", messageService.findUnReadMessageSysNum(user.getId(), CommunityConstant.TOPIC_LIKE));
            map.put("num", messageService.findMessageSysNum(user.getId(), CommunityConstant.TOPIC_LIKE));
            model.addAttribute("like", map);
        }
        if (latestFollow != null) {
            map = new HashMap<>();
            Map data = JSONObject.parseObject(HtmlUtils.htmlUnescape(latestFollow.getContent()), Map.class);
            map.put("latestFollow", latestFollow);
            map.put("entityType", data.get("entityType"));
            map.put("triggerId", data.get("userId"));
            map.put("trigger", userService.getUser((Integer) data.get("userId")));
            map.put("unReadNum", messageService.findUnReadMessageSysNum(user.getId(), CommunityConstant.TOPIC_FOLLOW));
            map.put("num", messageService.findMessageSysNum(user.getId(), CommunityConstant.TOPIC_FOLLOW));
            model.addAttribute("follow", map);
        }
        model.addAttribute("totUnReadCount", messageService.findUnreadCount(null, user.getId()));
        model.addAttribute("SysunReadNum", messageService.findUnReadMessageSysNum(user.getId(), null));
        return "/site/notice";
    }


    @MustLoginPage
    @RequestMapping(value = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable(value = "topic") String topic, Model model, MyPageHelper myPageHelper) {
        User user = hostHolder.getUser();
        int messageSysNum = messageService.findMessageSysNum(user.getId(), topic);
        myPageHelper.setLimit(5);
        myPageHelper.setTotalRow(messageSysNum);
        myPageHelper.setUrl("/notice/detail/" + topic);
        List<Message> messages = messageService.findLettersTo(user.getId(), topic, myPageHelper.getOffset(), myPageHelper.getLimit());
        if (messages.isEmpty())
            return "/site/notice-detail";
        List<HashMap<String, Object>> res = new LinkedList<>();
        List<Integer> unread=new LinkedList<>();
        for (Message message : messages) {
            if(message.getStatus()==0) unread.add(message.getId());
            HashMap<String, Object> map = new HashMap<>();
            map.put("sysHeaderUrl", userService.getUser(1).getHeaderUrl());
            Map content = JSONObject.parseObject(HtmlUtils.htmlUnescape(message.getContent()), Map.class);
            map.put("fromUser", userService.getUser(Integer.parseInt(content.get("userId").toString())));
            map.put("postId", content.get("postId"));
            map.put("createTime", message.getCreateTime());
            res.add(map);
        }
        if(!unread.isEmpty())
            messageService.changeStatus(unread,1);
        model.addAttribute("messages", res);
        return "/site/notice-detail";
    }
}
