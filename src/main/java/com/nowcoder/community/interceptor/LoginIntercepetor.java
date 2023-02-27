package com.nowcoder.community.interceptor;


import com.nowcoder.community.Bean.LoginTicket;
import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.Util.CookieUtil;
import com.nowcoder.community.Util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community-2022-08-04 17:10
 */
@Component
public class LoginIntercepetor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket = userService.loginTicketService(ticket);
            if(loginTicket==null || loginTicket.getStatus()==1 || loginTicket.getExpired().before(new Date())){
                return true;
            }
            User user = userService.getUser(loginTicket.getUserId());
            if(user==null || user.getStatus()==0){
                return true;
            }
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user!=null && modelAndView!=null)
            modelAndView.addObject("loginUser",user);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(hostHolder.getUser()!=null)
            hostHolder.remove();
    }
}
