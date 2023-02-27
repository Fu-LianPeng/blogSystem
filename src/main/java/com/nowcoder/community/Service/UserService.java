package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.LoginTicket;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Constant.ActivationResult;
import com.nowcoder.community.Mapper.LoginTicketMapper;
import com.nowcoder.community.Mapper.UserMapper;
import com.nowcoder.community.Util.MailClient;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-07-29 22:12
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    MailClient mailClient;

    @Value("${domain_name}")
    String domain;

    @Value("${context_path}")
    String contextPath;

    @Autowired
    RedisTemplate redisTemplate;

    public void registService(Map<String,String> map,User user){
        if(user==null) return;
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");return;
        }
        if(userMapper.selectByName(user.getUsername())!=null){
            map.put("usernameMsg","该用户名已被注册！");return;
        }
        if(userMapper.selectByEmail(user.getEmail())!=null){
            map.put("emailMsg","该邮箱已被注册！");return;
        }
        user.setType(0);
        user.setStatus(0);//未激活
        user.setActivationCode(StringUtils.getUUID());
        user.setSalt(StringUtils.getSalt());
        user.setPassword(StringUtils.MD5(user.getPassword()+user.getSalt()));
        user.setHeaderUrl("http://images.nowcoder.com/head/977t.png");
        userMapper.insert(user);
        HashMap<String, String> varible = new HashMap<>();
        varible.put("username",user.getUsername());
        varible.put("link",domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode());
        mailClient.sendHtmlMail(user.getEmail(),"激活邮件",
                varible,"/mail/activation");
        return;
    }
    public int activateService(int id,String activationCode){
        User user = userMapper.selectById(id);
        if(user==null){
            return ActivationResult.FAILURE;
        }else if(user.getStatus()==1){
            return ActivationResult.REPEATED;
        }else if(user.getActivationCode().equals(activationCode)){
            userMapper.updateStatus(id,1);
            return ActivationResult.SUCCESS;
        }else {
            return ActivationResult.FAILURE;
        }
    }

    public Map<String ,String> loginService(String username,String password,
                                            Date expired){
        Map<String ,String> map=new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");return map;
        }
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","用户不存在！");return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","用户未激活！");return map;
        }
        password=StringUtils.MD5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误！");return map;
        }
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(StringUtils.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(expired);

//        loginTicketMapper.insertTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logoutService(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket ticketBean = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        ticketBean.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,ticketBean);
    }

    public LoginTicket loginTicketService(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }
    public User getUser(int id){
        String userKey=RedisKeyUtil.getLoginUserKey(id);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        if(user==null){
            user=userMapper.selectById(id);
            redisTemplate.opsForValue().set(userKey,user);
            redisTemplate.expire(userKey,30*60+(int)(Math.random()*60), TimeUnit.SECONDS);
        }
        return user;
    }
    public User getUser(String username){
        return userMapper.selectByName(username);
    }

    public void changeHeader(String headerUrl,int userId){
        userMapper.updateHeaderUrl(userId,headerUrl);
    }
    public void changePassword(String password,int userId){
        userMapper.updatePassword(userId,password);
    }
}
