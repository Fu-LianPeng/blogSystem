package com.nowcoder.community.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.awt.*;
import java.util.Map;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-02 12:05
 */
@Component
public class MailClient {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    String from;

    public void sendSimpleMail(String to,String subject,String content){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(content);
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        mailSender.send(simpleMailMessage);
    }

    public void sendHtmlMail(String to, String subject,Map<String,String> varible,String template){
        Context context = new Context();
        for(Map.Entry<String,String> entry : varible.entrySet()){
            context.setVariable(entry.getKey(),entry.getValue());
        }
        String process = templateEngine.process(template, context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.setText(process,true);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessage.setSubject(subject);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
