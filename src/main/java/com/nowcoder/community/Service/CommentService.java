package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.Comment;
import com.nowcoder.community.Constant.CommunityConstant;
import com.nowcoder.community.Mapper.CommentMapper;
import com.nowcoder.community.Mapper.DiscussPostMapper;
import com.nowcoder.community.Util.TrieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-08-10 17:10
 */
@Service
public class CommentService {

    @Autowired
    TrieUtil trieUtil;

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public void addComment(Comment comment){
        if(comment==null){
            throw new IllegalArgumentException("comment为null");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(trieUtil.filterSensitiveWord(comment.getContent()));

        commentMapper.insertComment(comment);
        if(comment.getEntityType()== CommunityConstant.POSTTYPE)
            discussPostMapper.increaseCommentNum(comment.getEntityId());
    }

    /*不分页可以limit传0*/
    public List<Comment> findComment(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectComments(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCount(entityType,entityId);
    }



    public Comment findCommentById(int id){
        return commentMapper.selectComment(id);
    }
}
