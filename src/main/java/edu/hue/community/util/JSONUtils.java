package edu.hue.community.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author xiaoning
 * @date 2021/09/17
 * json 相关的工具类
 */
public class JSONUtils {

    /**
     * 将传入数据转换为 json 字符串
     * @param code 编号
     * @param msg 提示信息
     * @param map 数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
