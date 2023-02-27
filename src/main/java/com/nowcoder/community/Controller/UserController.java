package com.nowcoder.community.Controller;

import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Service.FollowService;
import com.nowcoder.community.Service.LikeService;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.annotation.MustLoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import sun.security.util.AuthResources_it;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-04 23:31
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    HostHolder hostHolder;
    @Value("${disk_file_path}")
    String uploadPath;

    @Value("${domain_name}")
    String domain_name;

    @Value("${context_path}")
    String context_path;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;


    @Autowired
    FollowService followService;

    @Autowired
    RedisTemplate redisTemplate;


    @MustLoginPage
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "/site/setting";
    }


    @RequestMapping(value = "/changeHeadImage", method = RequestMethod.POST)
    public String changeHead(MultipartFile headImage, Model model, HttpServletRequest request) {
        if (headImage == null) {
            model.addAttribute("error", "您必须上传一张图片！");
            return "/site/setting";
        }
        String filename = headImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || (!suffix.equals(".png") && !suffix.equals(".jpg"))) {
            model.addAttribute("error", "图片格式错误");
            return "/site/setting";
        }
        filename = StringUtils.getUUID() + suffix;
        try {
            headImage.transferTo(new File(uploadPath + "/" + filename));
            String headerUrl = "http://" + "127.0.0.1" + ":8080" + context_path + "/user" + "/img" + "/" + filename;
            if (hostHolder.getUser() == null)
                return "redirect:/index";
            userService.changeHeader(headerUrl, hostHolder.getUser().getId());
            String userKey= RedisKeyUtil.getLoginUserKey(hostHolder.getUser().getId());
            redisTemplate.delete(userKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/index";
    }

    @RequestMapping(value = "/img/{filename}", method = RequestMethod.GET)
    public void getHeaderImg(@PathVariable("filename") String filename, HttpServletResponse response) {
        try (FileInputStream fileInputStream = new FileInputStream(uploadPath + "/" + filename);
             ServletOutputStream outputStream = response.getOutputStream();) {
            byte[] bytes = new byte[1024];
            while (fileInputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, Model model) {
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            model.addAttribute("errorOldPassword", "密码不能为空");
            model.addAttribute("errorNewPassword", "密码不能为空");
            return "/site/setting";
        }
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("errorNewPassword", "请使用不同的新密码");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(StringUtils.MD5(oldPassword + user.getSalt()))) {
            model.addAttribute("errorOldPassword", "原密码错误");
            return "/site/setting";
        }
        userService.changePassword(StringUtils.MD5(newPassword + user.getSalt()), user.getId());
        String userKey= RedisKeyUtil.getLoginUserKey(hostHolder.getUser().getId());
        redisTemplate.delete(userKey);
        return "redirect:/logout";
    }

    @MustLoginPage
    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String getPersonalHomePage(@PathVariable(value = "userId") int userId, Model model) {

        model.addAttribute("user", userService.getUser(userId));
        model.addAttribute("likeNum", likeService.getUserLikeNum(userId));
        User user = hostHolder.getUser();
        if (hostHolder.getUser().getId() == userId)//自己访问自己的主页
        {
            model.addAttribute("MyHomePage", 1);
        } else {
            model.addAttribute("isFollow", followService.getFollowStatus(3, userId, user.getId()));
        }
        model.addAttribute("followerNum", followService.getFollowerNum(3, userId));
        model.addAttribute("followNum", followService.getFolloweeNum(3, userId));
        return "/site/profile";
    }
    
}