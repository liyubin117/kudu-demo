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
public class JsonStr2Map {
    /**
     * 将Json字符串转为Map对象
     * @param jsonStr
     * @return
     */
    public static Map<String, String> jsonStr2Map(String jsonStr) {
        Map<String, String> resultMap = new HashMap<>();
//        Pattern pattern = Pattern.compile("(\"\\w+\"):(\"[^\"]+\")");
//        Matcher m = pattern.matcher(jsonStr);
//        String[] strs = null;
//        while (m.find()) {
//            strs = m.group().split(":");
//            if(strs != null && strs.length == 2) {
//                resultMap.put(strs[0].replaceAll("\"", "").trim(), strs[1].trim().replaceAll("\"", ""));
//            }
//        }


        JSONObject jo = JSONObject.parseObject(jsonStr);
        for(Map.Entry entry:jo.entrySet()){
            resultMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return resultMap;
    }
}