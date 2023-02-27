package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Service.ElasticsearchService;
import com.nowcoder.community.Service.LikeService;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.MyPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-09-08 23:00
 */
@Controller
public class SearchController {

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path="/search",method = RequestMethod.GET)
    public String search(String keyword, MyPageHelper myPageHelper, Model model){


        ArrayList<DiscussPost> discussPosts = elasticsearchService.searchDiscussPost(keyword, myPageHelper.getCurrentPage() - 1, myPageHelper.getLimit());


        List<Map<String,Object>> list=new ArrayList<>();

        for (DiscussPost post : discussPosts) {
            Map<String ,Object> map=new HashMap<>();

            map.put("post",post);
            map.put("user",userService.getUser(post.getUserId()));
            map.put("likeCount",likeService.getLikeNum(CommunityConstant.POSTTYPE,post.getId()));

            list.add(map);
        }
        model.addAttribute("discussPosts",list);
        model.addAttribute("keyword",keyword);

        myPageHelper.setUrl("/search?keyword="+keyword);
        myPageHelper.setTotalRow(discussPosts.size());
        return "/site/search";
    }
}
