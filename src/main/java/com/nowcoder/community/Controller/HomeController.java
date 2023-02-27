package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Mapper.DiscussPostMapper;
import com.nowcoder.community.Mapper.UserMapper;
import com.nowcoder.community.Service.*;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.MyPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-07-29 22:13
 */
@Controller
public class HomeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    HomeService homeService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    private MessageService messageService;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping("/index")
    public String mainPage(Model model, MyPageHelper page, @RequestParam(name = "OrderMode", defaultValue = "0") int OrderMode) {
        page.setTotalRow(discussPostService.getRowsNumber(0));
        page.setUrl("/index?OrderMode="+OrderMode);
        List<DiscussPost> posts = discussPostService.getDiscussPosts(0,page.getOffset(), page.getLimit(),OrderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>(posts.size());
        for (DiscussPost post : posts) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", userService.getUser(post.getUserId()));
            hashMap.put("post", post);
            hashMap.put("commentNum", discussPostService.findPostCommentCount(post.getId()));
            hashMap.put("likeNum", likeService.getLikeNum(CommunityConstant.POSTTYPE, post.getId()));
            discussPosts.add(hashMap);
        }
        model.addAttribute("discussPosts", discussPosts);
        User user = hostHolder.getUser();
        if (user == null)
            model.addAttribute("unReadMessage", 0);
        else
            model.addAttribute("unReadMessage", messageService.findUnreadCount(null, user.getId()) + messageService.findUnReadMessageSysNum(user.getId(), null));
        model.addAttribute("OrderMode", OrderMode);
        return "index";
    }

}
