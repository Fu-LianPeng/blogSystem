package com.nowcoder.community.Advice;

import com.nowcoder.community.Util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Advice-2022-08-16 21:17
 */
@ControllerAdvice(annotations = Controller.class)
public class ControllerException {


    Logger logger= LoggerFactory.getLogger(ControllerException.class);
    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception e,HttpServletResponse response, HttpServletRequest request){
        StringBuilder errorStack=new StringBuilder();
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            errorStack.append(stackTraceElement.toString());
        }
        e.printStackTrace();
        logger.error("controller 错误:",errorStack.toString());
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))){
            try {
                response.setContentType("application/plain;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.print(StringUtils.getJsonString(1,"服务器异常"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            try {
                response.sendRedirect(request.getContextPath()+"/error");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
