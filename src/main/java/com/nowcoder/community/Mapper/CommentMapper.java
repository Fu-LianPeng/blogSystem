package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.Comment;
import com.nowcoder.community.Constant.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-08-09 19:45
 */
@Mapper
public interface CommentMapper {

    public int selectCount(int entityType,int entityId);

    public List<Comment> selectComments(int entityType,int entityId,int offset,int limit);

    public int insertComment(Comment comment);

    public Comment selectComment(int id);
}
