package com.nowcoder.community.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-04 17:15
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request,String key){
        if(request==null || key==null){
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(key)){
                    return cookie.getValue();
                }
            }
        return null;
    }
}
