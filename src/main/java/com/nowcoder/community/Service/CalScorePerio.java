package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-12-11 19:03
 */
@Service
public class CalScorePerio implements InitializingBean {


    Logger logger = LoggerFactory.getLogger(CalScorePerio.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    LikeService likeService;
    @Autowired
    DiscussPostService discussPostService;

    static {
        try {
            baseTime = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss").parse("2022-1-1 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static Date baseTime;

    private void refresh(int postId) {
        double G = 3;
        DiscussPost post = discussPostService.getPost(postId);
        if (post == null) return;
        int commentCount = post.getCommentCount();
        Long likeNum = likeService.getLikeNum(CommunityConstant.POSTTYPE, postId);
        double score =Math.log10(5 * likeNum + 10 * commentCount+10)+(post.getCreateTime().getTime()-baseTime.getTime())/(12*60*60*1000);
        discussPostService.updatePostScore(postId, score);
        logger.info(postId+"refresh");
    }
    public void loadHotPostToCache(){

    }
    @Override
    public void afterPropertiesSet() throws Exception {

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        System.out.println("start Thread" + new Date());
        executor.scheduleAtFixedRate(() -> {
            String key = RedisKeyUtil.getPostScore();
            BoundSetOperations operations = redisTemplate.boundSetOps(key);
            if (operations.size() == 0) {
                logger.info("not element to refresh");
                return;
            }
            while (operations.size() > 0) {
                refresh((int) operations.pop());
            }
        }, 0, 5, TimeUnit.MINUTES);
    }
}
