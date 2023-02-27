package com.nowcoder.community.config;

import com.nowcoder.community.interceptor.ForceLoginInterceptor;
import com.nowcoder.community.interceptor.LoginIntercepetor;
import com.nowcoder.community.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.config-2022-08-04 20:58
 */
@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginIntercepetor loginIntercepetor;
    @Autowired
    ForceLoginInterceptor forceLoginInterceptor;
    @Autowired
    MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntercepetor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png");
        registry.addInterceptor(forceLoginInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png");
    }
}
