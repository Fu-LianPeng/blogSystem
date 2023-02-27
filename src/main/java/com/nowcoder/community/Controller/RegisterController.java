package com.nowcoder.community.Controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.ActivationResult;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author fulianpeng
 * @create com.nowcoder.community.Controller-2022-08-02 11:22
 */

@Controller
public class RegisterController {


    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String regist(User user, Map<String, String> map) {
        userService.registService(map, user);
        if (map.size() == 2) {
            map.put("msg", "注册成功");
            map.put("jump_link", "/index");
            return "/site/operate-result";
        } else {
            return "/site/register";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String verifycode,
                        boolean rememberMe,  Model model, HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner) {

        if(kaptchaOwner==null){
            model.addAttribute("verifycodeMsg", "验证码错误！");
            return "/site/login";
        }
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        String text = (String) redisTemplate.opsForValue().get(kaptchaKey);
        if ( StringUtils.isBlank(verifycode) || !verifycode.equalsIgnoreCase(text)) {
            model.addAttribute("verifycodeMsg", "验证码错误！");
            return "/site/login";
        }
        Date expired = new Date(System.currentTimeMillis() + (rememberMe ? (long)3600000 * 24 * 30 : (long)3600000 * 24));
        Map<String, String> map = userService.loginService(username, password, expired);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/community");
            cookie.setMaxAge(rememberMe ? 3600 * 24 * 30 : 3600 * 24);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
            return "/site/login";
        }
    }
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String  ticket){
        userService.logoutService(ticket);
        return "redirect:/login";
    }

    //http://localhost:8080/community/activation/159/326368df-25e0-468f-abd3-f9473173eee1
    @RequestMapping(value = "/activation/{userId}/{activationCode}", method = RequestMethod.GET)
    public String activate(Model model, @PathVariable(value = "userId") int userId, @PathVariable(value = "activationCode") String activationCode) {
        int activateRes = userService.activateService(userId, activationCode);
        if (activateRes == ActivationResult.SUCCESS) {
            model.addAttribute("msg", "激活成功");
            model.addAttribute("jump_link", "/login");
            return "/site/operate-result";
        } else if (activateRes == ActivationResult.REPEATED) {
            model.addAttribute("msg", "请勿重复激活");
            model.addAttribute("jump_link", "/login");
            return "/site/operate-result";
        } else {
            model.addAttribute("msg", "激活失败");
            model.addAttribute("jump_link", "/index");
            return "/site/operate-result";
        }
    }

    @Autowired
    Producer producer;

    @Autowired
    RedisTemplate redisTemplate;

    @Value(value = "context_path")
    String context_path;

    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        String owner = StringUtils.getUUID();
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);
        Cookie cookie = new Cookie("kaptchaOwner", owner);
        cookie.setMaxAge(60);
        cookie.setPath(context_path);
        response.addCookie(cookie);
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
