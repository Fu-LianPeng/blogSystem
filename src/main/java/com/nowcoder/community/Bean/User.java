package com.nowcoder.community.Bean;

import lombok.Data;

import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Bean-2022-07-27 21:13
 */
@Data
public class User {
    private int  id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;

}
