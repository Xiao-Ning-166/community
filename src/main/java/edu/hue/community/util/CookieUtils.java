package edu.hue.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 47552
 * @date 2021/09/16
 * Cookie操作的工具类
 */
public class CookieUtils {

    /**
     * 从Cookie中获取特定值
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        // 空值判断
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数不能为空！！！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 遍历Cookie，寻找特定值
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
