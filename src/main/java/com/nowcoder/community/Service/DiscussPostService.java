package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.Comment;
import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Mapper.CommentMapper;
import com.nowcoder.community.Mapper.DiscussPostMapper;
import com.nowcoder.community.Util.RedisKeyUtil;
import com.nowcoder.community.Util.StringUtils;
import com.nowcoder.community.Util.TrieUtil;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-07-29 21:58
 */
@Service
public class DiscussPostService {

    Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    TrieUtil trieUtil;

    @Value("${rediscache.pageNum}")
    private int cachePostNum;

    @Autowired
    RedisTemplate redisTemplate;

    public List<DiscussPost> getDiscussPosts(int userId, int offset, int limit, int OrderMode) {
        if (userId == 0 && OrderMode == 1 && offset / limit + 1 <= cachePostNum) {
            String pagekey = RedisKeyUtil.getHotPost(offset);
            List<DiscussPost> posts = (List<DiscussPost>) redisTemplate.opsForValue().get(pagekey);
            if (posts == null) {
                List<DiscussPost> postsList = discussPostMapper.selectById(userId, offset, limit, OrderMode);
                redisTemplate.opsForValue().set(pagekey, postsList, 3 * 60, TimeUnit.SECONDS);
                return postsList;
            }
            logger.info("select from cache");
            return posts;
        }
        return discussPostMapper.selectById(userId, offset, limit, OrderMode);
    }

    public int getRowsNumber(int userId) {
        String rowsKey = RedisKeyUtil.getPostRows();
        Integer rows = (Integer) redisTemplate.opsForValue().get(rowsKey);
        if (rows == null) {
            logger.info("数据库查询------------------------");
            rows = discussPostMapper.selectRows(userId);
            redisTemplate.opsForValue().set(rowsKey, rows,3,TimeUnit.MINUTES);
        }
        return rows;
    }

    public void insertPost(DiscussPost post) {

        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        post.setTitle(trieUtil.filterSensitiveWord(post.getTitle()));
        post.setContent(trieUtil.filterSensitiveWord(post.getContent()));
        discussPostMapper.insertPost(post);
    }

    public void updatePostScore(int id, double score) {
        discussPostMapper.updateScore(id, score);
    }

    public DiscussPost getPost(int id) {
        return discussPostMapper.select(id);
    }

    /**
     * 找帖子的评论数量直接用
     *
     * @return
     */
    public int findPostCommentCount(int postId) {
        String rowsKey = RedisKeyUtil.getPostRows(postId);
        Integer rows = (Integer) redisTemplate.opsForValue().get(rowsKey);
        if (rows == null) {
            logger.info("数据库查询------------------------");
            rows = discussPostMapper.getCommentCount(postId);
            redisTemplate.opsForValue().set(rowsKey, rows,3,TimeUnit.MINUTES);
        }
        return rows;
    }

}
