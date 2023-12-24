package com.ourProject.StudyAnalysis.service;

import com.ourProject.StudyAnalysis.entity.Warning;
import com.ourProject.StudyAnalysis.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudyService {
    @Autowired
    RedisTemplate redisTemplate;
    public void updateFile(int classId,String filePath){
        String classKey = RedisKeyUtil.getClassFileKey(classId);
        redisTemplate.opsForList().leftPush(classKey,filePath);
    }
    public void addStudyTime(int studentId,int classId){
        String key = RedisKeyUtil.getStudyKey(classId,studentId);
        String allKey = RedisKeyUtil.getAllStudyTimeKey(classId,studentId);
        redisTemplate.opsForValue().increment(allKey);
        redisTemplate.opsForValue().increment(key);
    }
    public int findAllStudyTime(int classId,int studentId){
        String key = RedisKeyUtil.getAllStudyTimeKey(classId,studentId);
        Integer integer = (Integer) redisTemplate.opsForValue().get(key);
        if(integer == null){
            return 0;
        }
        return integer;
    }
    public void warning(int classId,int studentId){
        String key = RedisKeyUtil.getWarningKey(studentId);
        Warning warning = new Warning();
        warning.setMsg("您的学习时间过短");
        warning.setClassId(classId);
        redisTemplate.opsForList().leftPush(key,warning);
    }
    public List<Warning> findWarning(int studentId){
        String key = RedisKeyUtil.getWarningKey(studentId);
        Long size = redisTemplate.opsForList().size(key);
        List<Warning> range = redisTemplate.opsForList().range(key, 0, size);
        return range;
    }
    public int findStudyTimeByMonthByDay(int month,int day,int classId,int studentId){
        String key = RedisKeyUtil.getMonthDayKey(month, day, classId, studentId);
        Integer o = (Integer) redisTemplate.opsForValue().get(key);
        if(o == null){
            return 0;
        }
        return o;
    }
}
