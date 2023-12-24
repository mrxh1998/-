package com.ourProject.StudyAnalysis.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.*;

public class StudyAnalysisUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map != null){
            for(String key:map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }
    public static List<Integer> getNowMonthDayList(){
        Calendar now = Calendar.getInstance();
        int day = now.getActualMaximum(Calendar.DATE);
        List<Integer> month = new ArrayList<>();
        for(int i = 1 ; i <= day;i++){
            month.add(i);
        }
        return month;
    }
    public static int getNowMonth(){
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH)+1;
        return month;
    }
}
