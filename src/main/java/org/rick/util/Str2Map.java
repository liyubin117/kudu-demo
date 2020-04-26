package org.rick.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liyubin
 * @version 1.0
 * @company Netease
 * @description
 */
public class Str2Map {
    /**
     * 将Json字符串转为Map对象
     * @param jsonStr
     * @return
     */
    public static Map<String, String> jsonStr2Map(String jsonStr) {
        Map<String, String> resultMap = new HashMap<>();

        JSONObject jo = JSONObject.parseObject(jsonStr);
        for(Map.Entry entry:jo.entrySet()){
            resultMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return resultMap;
    }

    public static Map<String, String> csvStr2Map(String str) {
        Map<String, String> resultMap = new HashMap<>();

        for(String entry:str.split(",")){
            String[] tmp = entry.split(":");
            resultMap.put(tmp[0], tmp[1]);
        }
        return resultMap;
    }
}