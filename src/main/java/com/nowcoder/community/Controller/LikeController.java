package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.Event;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.LikeService;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.annotation.MustLoginPage;
import com.nowcoder.community.event.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-22 23:43
 */
@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int userIdOwnEntity, int postId) {
        User user = hostHolder.getUser();
        if (user == null) return StringUtils.getJsonString(1, "请登陆后点赞");
        likeService.like(user.getId(), entityType, entityId, userIdOwnEntity);

        HashMap<String, Object> map = new HashMap<>();
        //System.out.println(likeService.getLikeNum(entityType, entityId));
        map.put("likeNum", likeService.getLikeNum(entityType, entityId));
        int likeStatus = likeService.getLikeStatus(user.getId(), entityType, entityId);
        map.put("likeStatus", likeStatus);

        if (likeStatus == 1) {
            Event event = new Event()
                    .setTriggerId(user.getId())
                    .setTopic(CommunityConstant.TOPIC_LIKE)
                    .setToId(userIdOwnEntity)
                    .setAboutEntityType(entityType)
                    .setAboutEntityId(entityId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
            if (entityType == CommunityConstant.POSTTYPE) {
                String key = RedisKeyUtil.getPostScore();
                redisTemplate.opsForSet().add(key,postId);
            }
        }
        return StringUtils.getJsonString(0, null, map);
    }
}
