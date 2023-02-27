package com.nowcoder.community.Bean;

import lombok.Data;

import java.util.Date;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Bean-2022-08-04 10:25
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String  ticket;
    private int status;
    private Date expired;
}
