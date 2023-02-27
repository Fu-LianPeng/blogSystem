package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.Event;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.FollowService;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.MyPageHelper;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.annotation.MustLoginPage;
import com.nowcoder.community.event.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-24 21:52
 */
@Controller
public class FollowController {

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    @MustLoginPage
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        if(entityType == CommunityConstant.USERTYPE && user.getId()==entityId)
            return StringUtils.getJsonString(1,"用户不能关注自己");
        followService.follow(entityType,entityId,user.getId());
        HashMap map=new HashMap();
        boolean followStatus = followService.getFollowStatus(entityType, entityId, user.getId());
        map.put("followStatus",followStatus);

        if(followStatus){
            Event event = new Event()
                    .setTopic(CommunityConstant.TOPIC_FOLLOW)
                    .setTriggerId(user.getId())
                    .setToId(entityId)
                    .setAboutEntityType(entityType)
                    .setAboutEntityId(entityId);
            eventProducer.fireEvent(event);
        }


        return StringUtils.getJsonString(0,null,map);
    }

    @RequestMapping(value = "/followee/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable(value = "userId") int userId, Model model, MyPageHelper pageHelper){
        pageHelper.setTotalRow((int) followService.getFolloweeNum(CommunityConstant.USERTYPE,userId));
        pageHelper.setUrl("/followee/"+userId);
        List<Map<String, Object>> followees = followService.getFollowees(CommunityConstant.USERTYPE, userId,pageHelper.getOffset(),pageHelper.getLimit());
        for (Map<String, Object> followee : followees) {
            followee.put("followStat",getFollowStatus((User) followee.get("user")));
        }
        model.addAttribute("followees",followees);
        model.addAttribute("user", userService.getUser(userId));
        return "/site/followee";
    }


    @RequestMapping(value = "/follower/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable(value = "userId") int userId, Model model, MyPageHelper pageHelper){
        pageHelper.setTotalRow((int) followService.getFollowerNum(CommunityConstant.USERTYPE,userId));
        pageHelper.setUrl("/follower/"+userId);
        List<Map<String, Object>> followers = followService.getFollowers(CommunityConstant.USERTYPE, userId,pageHelper.getOffset(),pageHelper.getLimit());
        for (Map<String, Object> follower : followers) {
            follower.put("followStat",getFollowStatus((User) follower.get("user")));
        }
        model.addAttribute("followers",followers);
        model.addAttribute("user", userService.getUser(userId));
        return "/site/follower";
    }

    private boolean getFollowStatus(User user){
        User loginUser = hostHolder.getUser();
        if (loginUser==null) return false;
        return followService.getFollowStatus(CommunityConstant.USERTYPE, user.getId(), loginUser.getId());
    }
}
