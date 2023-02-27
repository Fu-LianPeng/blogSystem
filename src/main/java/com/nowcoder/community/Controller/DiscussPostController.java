package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.Comment;
import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Bean.Event;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Mapper.UserMapper;
import com.nowcoder.community.Service.*;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.MyPageHelper;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.annotation.MustLoginPage;
import com.nowcoder.community.event.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-08 12:00
 */
@Controller
@RequestMapping(value = "/discuss")
public class DiscussPostController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;
    @ResponseBody
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String addPost(String title,String content){
        if(StringUtils.isBlank(title))
            return StringUtils.getJsonString(449,"标题不要为空！");
        if(StringUtils.isBlank(content))
            return StringUtils.getJsonString(449,"内容不要为空！");
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setUserId(hostHolder.getUser().getId());
        discussPost.setCreateTime(new Date());
        discussPostService.insertPost(discussPost);
        Event event = new Event();
        event.setTopic(CommunityConstant.TOPIC_PUBLISH);
        event.setData("post",discussPost);
        eventProducer.fireEvent(event);
        String key= RedisKeyUtil.getPostScore();
        redisTemplate.opsForSet().add(key,discussPost.getId());
        return StringUtils.getJsonString(0,"发布成功");
    }



    @RequestMapping(value = "/detail/{id}",method = RequestMethod.GET)
    public String getPost(@PathVariable("id") int id, Model model, MyPageHelper pageHelper){
        pageHelper.setUrl("/discuss/detail/"+id);
        DiscussPost post = discussPostService.getPost(id);
        int commentRow=post.getCommentCount();
        pageHelper.setTotalRow(commentRow);
        User curUser = hostHolder.getUser();
        model.addAttribute("post",post);
        model.addAttribute("likeStatus",curUser==null?0:likeService.getLikeStatus(curUser.getId(),CommunityConstant.POSTTYPE,post.getId()));
        model.addAttribute("likeNum",likeService.getLikeNum(CommunityConstant.POSTTYPE,post.getId()));
        User user = userService.getUser(post.getUserId());
        model.addAttribute("user",user);

        List<Comment> postComments = commentService.findComment(CommunityConstant.POSTTYPE,
                id, pageHelper.getOffset(),pageHelper.getLimit());

        ArrayList<Map<String,Object>> postCommentUserLists=new ArrayList<>();

        for (Comment postComment : postComments) {
            Map<String,Object> postCommentUser =new HashMap<>();
            postCommentUser.put("likeStatus",curUser==null?0:likeService.getLikeStatus(curUser.getId(),CommunityConstant.COMMENTTYPE,postComment.getId()));
            postCommentUser.put("likeNum",likeService.getLikeNum(CommunityConstant.COMMENTTYPE,postComment.getId()));
            postCommentUser.put("comment",postComment);
            postCommentUser.put("user",userService.getUser(postComment.getUserId()));
            List<Comment> replys = commentService.findComment(CommunityConstant.COMMENTTYPE, postComment.getId(), 0, 0);
            ArrayList<Map<String,Object>> replyUserLists=new ArrayList<>();
            for (Comment reply : replys) {
                Map<String,Object> replyUser =new HashMap<>();
                replyUser.put("likeStatus",curUser==null?0:likeService.getLikeStatus(curUser.getId(),CommunityConstant.COMMENTTYPE,reply.getId()));
                replyUser.put("likeNum",likeService.getLikeNum(CommunityConstant.COMMENTTYPE,reply.getId()));
                replyUser.put("reply",reply);
                replyUser.put("user",userService.getUser(reply.getUserId()));
                replyUser.put("target",userService.getUser(reply.getTargetId()));
                replyUserLists.add(replyUser);
            }
            postCommentUser.put("replyNum",commentService.findCommentCount(CommunityConstant.COMMENTTYPE,postComment.getId()));
            postCommentUser.put("replyLists",replyUserLists);
            postCommentUserLists.add(postCommentUser);
        }

        model.addAttribute("commentLists", postCommentUserLists);
        model.addAttribute("commentNum",commentRow);
        return "/site/discuss-detail";
    }


}
