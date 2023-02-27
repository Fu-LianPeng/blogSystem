package com.nowcoder.community.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-02 11:39
 */
public class StringUtils {
    public static boolean isBlank(String str) {
        return str == null || str.length() == 0 || str.matches("\\s*");
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getSalt() {
        return getUUID().substring(0, 5);
    }

    public static String MD5(String key_salt) {
        String md5Password = DigestUtils.md5DigestAsHex(key_salt.getBytes());
        return md5Password;
    }

    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", msg);
            if (map != null)
                for (String key : map.keySet()) {
                    jsonObject.put(key, map.get(key));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public static String getJsonString(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
