package com.nowcoder.community;

import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Service.DiscussPostService;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.sun.mail.util.logging.MailHandler;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;
import sun.nio.cs.FastCharsetProvider;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.security.Policy;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DiscussPostService discussPostService;
    @Test
    void add() throws InterruptedException {
        for (int i = 0; i < 3000; i++) {
            Thread.sleep(100);
            DiscussPost post = new DiscussPost();
            post.setCreateTime(new Date());
            post.setTitle("插入的新帖子"+i);
            post.setContent("本书以丰富的史料和周密的考证分析，对中国中古历史中的门阀政治问题作了再探索，认为中外学者习称的魏晋南北朝门阀政治，实际上只存在于东晋一朝；门阀政治是皇权政治在特定历史条件下出现的变态，具有暂时性和过渡性，其存在形式是门阀士族与皇权的共治。本书不落以婚宦论门阀士族的窠臼，对中国中古政治史中的这一重要问题提供了精辟的见解，具有很高的学术价值。");
            post.setStatus(0);
            post.setType(0);
            discussPostService.insertPost(post);
            String key = RedisKeyUtil.getPostScore();
            redisTemplate.opsForSet().add(key,post);
        }

    }
    @Test
    void contextLoads() {
        System.out.println(username);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo("21120045@bjtu.edu.cn");
        simpleMailMessage.setSubject("Test");
        simpleMailMessage.setText("welcome!");
        mailSender.send(simpleMailMessage);
    }
    @Test
    void testMimeEmail(){
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo("21120045@bjtu.edu.cn");
            mimeMessageHelper.setSubject("Test2");
            mimeMessageHelper.setText("请查收");
            mimeMessageHelper.addAttachment("附件1",new FileSystemResource(new File("D:\\nowcoder_community\\所有素材和源码\\所有素材和源码\\nowcoder\\index.html")));
            MimeMessage mimeMessage = mimeMessageHelper.getMimeMessage();
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    @Autowired
    TemplateEngine templateEngine;
    @Test
    void testMailHtml(){
        Context context = new Context();
        context.setVariable("username", "xiaoming");
        String  process= templateEngine.process("/mail/mail", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        System.out.println(process);
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo("21120045@bjtu.edu.cn");
            mimeMessageHelper.setSubject("TestHtml");
            mimeMessageHelper.setText(process,true);
            MimeMessage mimeMessage1 = mimeMessageHelper.getMimeMessage();
            mailSender.send(mimeMessage1);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {

    }

}
