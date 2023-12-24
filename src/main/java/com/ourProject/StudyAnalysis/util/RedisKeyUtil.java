package com.ourProject.StudyAnalysis.util;

import java.util.Calendar;
import java.util.Date;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKER = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_FILE = "file";
    private static final String PREFIX_STUDY = "study";
    private static final String PREFIX_WARNING = "warning";
    private static final String PREFIX_MESSAGE = "message";
    //通知key
    public static String getMessageKey(int classId){
        return PREFIX_MESSAGE + SPLIT + classId;
    }
    //根据月份天数拿key
    public static String getMonthDayKey(int month,int day,int classId,int studentId){
        return PREFIX_STUDY + SPLIT + classId + SPLIT + studentId + SPLIT + month + SPLIT + day;
    }
    //预警key
    public static String getWarningKey(int studentId){
        return PREFIX_WARNING  + SPLIT + studentId;
    }
    //总学习时长key
    public  static String getAllStudyTimeKey(int classId,int studentId){
        return PREFIX_STUDY+SPLIT + classId + SPLIT + studentId;
    }
    //学习时长key
    public static String getStudyKey(int classId,int studentId){
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH)+1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return PREFIX_STUDY + SPLIT + classId + SPLIT + studentId + SPLIT + month + SPLIT + day;
    }
    //班级存放文件key
    public static String getClassFileKey(int classId){
        return PREFIX_FILE + SPLIT + classId;
    }
    //某个实体的赞
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    //某个用户获得的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
    //某个学生加入的班级
    //followee:userId:entityType->zset(entityId,now)
    public static String getFollowee(int userId){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT;
    }
    //某个班级的学生
    //follower:entityType:entityId->zset(userId,now)
    public static String getFollower(int cLassId){
        return PREFIX_FOLLOWER +SPLIT  + SPLIT + cLassId;
    }

    //验证码key

    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //登陆凭证key
    public static  String getTicketKey(String ticket){
        return PREFIX_TICKER+SPLIT+ticket;
    }

    //用户key
    public static  String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
