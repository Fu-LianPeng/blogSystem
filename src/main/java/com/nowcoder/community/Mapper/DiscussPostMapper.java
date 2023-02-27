package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.DiscussPost;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-07-29 21:30
 */
@Mapper
public interface DiscussPostMapper {

    public List<DiscussPost> selectById(int userId,int offset, int limit,int OrderMode);

    public int selectRows(int userId);


    @Insert({
            "insert into discuss_post(user_id,title,content,type,status,create_time,comment_count,score)",
            "values(#{userId},#{title},#{content},#{type},#{status},#{createTime},#{commentCount},#{score})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertPost(DiscussPost post);


    public DiscussPost select(int id);


    public int increaseCommentNum(int id);

    public int getCommentCount(int id);

    void updateScore(int id, double score);
}
