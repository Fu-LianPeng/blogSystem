package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-07-27 21:23
 */
@Mapper
public interface UserMapper {

    public User selectById(int  id);

    public User selectByName(String username);

    public User selectByEmail(String email);

    public int insert(User user);

    public int updatePassword(int id,String password);

    public int updateHeaderUrl(int id,String headerUrl);

    public int updateStatus(int id,int status);
}
