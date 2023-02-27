package com.nowcoder.community.Util;

import com.nowcoder.community.Bean.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-04 20:42
 */
@Component
public class HostHolder {

    private ThreadLocal<User> threadLocal=new ThreadLocal<>();

    public User getUser(){
        User user = threadLocal.get();
        return user;
    }
    public void setUser(User user){
        threadLocal.set(user);
    }
    public void remove(){
        threadLocal.remove();
    }

}
