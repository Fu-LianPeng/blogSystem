package com.nowcoder.community.Service;

import com.nowcoder.community.Bean.DiscussPost;
import com.nowcoder.community.Mapper.DiscussPostMapper;
import com.nowcoder.community.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-08-09 17:17
 */
@Service
public class HomeService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;

    public int findDiscussCount(){
        return discussPostMapper.selectRows(0);
    }
    public List<DiscussPost> findDiscuss(int offset,int limit,int OrderMode){

        return discussPostMapper.selectById(0, offset, limit,OrderMode);
    }
}
