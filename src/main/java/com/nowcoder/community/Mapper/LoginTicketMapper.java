package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-08-04 10:36
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    public LoginTicket selectByTicket(String ticket);

    @Update({
            "update login_ticket set status = #{status} ",
            "where ticket = #{ticket}"
    })
    public int updateStatus(String ticket,int status);
}
