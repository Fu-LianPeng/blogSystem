package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.Comment;
import com.nowcoder.community.Bean.Event;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.CommentService;
import com.nowcoder.community.Service.DiscussPostService;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.annotation.MustLoginPage;
import com.nowcoder.community.event.EventProducer;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-10 17:15
 */

@Controller
@RequestMapping(value = "/comment")
public class CommentController {


    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/add/{postId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        if(hostHolder.getUser()==null)
            return "redirect:/login";
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);

        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_COMMENT)
                .setTriggerId(hostHolder.getUser().getId())
                .setAboutEntityType(comment.getEntityType())
                .setAboutEntityId(comment.getId())
                .setData("postId",postId);

        if(comment.getEntityType()==CommunityConstant.COMMENTTYPE){
            event.setToId(comment.getTargetId());
        }else{
            String key= RedisKeyUtil.getPostScore();
            redisTemplate.opsForSet().add(key,postId);
            event.setToId(discussPostService.getPost(postId).getUserId());
        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/"+postId;
    }

}
