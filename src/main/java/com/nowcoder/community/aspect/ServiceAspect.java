package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * @author fulianpeng
 * @create com.nowcoder.community.aspect-2022-08-16 22:19
 */
@Component
@Aspect
public class ServiceAspect {

    Logger logger=LoggerFactory.getLogger(ServiceAspect.class);

    @Pointcut("execution(* com.nowcoder.community.Service.*.*(..))")
    public void pointCut(){};

    @Before("pointCut()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null) return;
        HttpServletRequest request = requestAttributes.getRequest();
        String remoteHost = request.getRemoteHost();
        Signature signature = joinPoint.getSignature();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = simpleDateFormat.format(new Date());
        logger.info(remoteHost+"在"+time+"访问了"+signature.getDeclaringTypeName()+"."+signature.getName());
    }

}
