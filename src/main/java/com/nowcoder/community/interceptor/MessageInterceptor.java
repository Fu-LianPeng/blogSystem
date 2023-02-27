package com.nowcoder.community.interceptor;

import com.nowcoder.community.Bean.User;
import com.nowcoder.community.Service.MessageService;
import com.nowcoder.community.Util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.interceptor-2022-09-05 16:13
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {


    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            int unReadMessageSysNum = messageService.findUnReadMessageSysNum(user.getId(), null);
            int unreadLetterCount = messageService.findUnreadCount(null, user.getId());
            modelAndView.addObject("unReadMessage",unreadLetterCount+unReadMessageSysNum);
        }
    }
}
