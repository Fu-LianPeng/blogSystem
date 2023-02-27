package com.nowcoder.community.interceptor;

import com.nowcoder.community.Util.HostHolder;
import com.nowcoder.community.annotation.MustLoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.interceptor-2022-08-05 23:10
 */
@Component
public class ForceLoginInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/"));
        if(hostHolder.getUser()!=null&&url.equals("/login")){
            response.sendRedirect(request.getContextPath()+"/index");
            return false;
        }
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            if(handlerMethod.hasMethodAnnotation(MustLoginPage.class) && hostHolder.getUser()==null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
