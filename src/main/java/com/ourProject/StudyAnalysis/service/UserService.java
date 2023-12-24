package com.ourProject.StudyAnalysis.service;

import com.ourProject.StudyAnalysis.dao.UserMapper;
import com.ourProject.StudyAnalysis.entity.LoginTicket;
import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.util.RedisKeyUtil;
import com.ourProject.StudyAnalysis.util.StudyAnalysisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;
    public Map addUser(User user,int type){
        HashMap<String, Object> map = new HashMap<>();
        //判断数据合法性
        if(user == null){
            throw new IllegalArgumentException("User不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getUserPassword())){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        //验证账号是否存在
        if(userMapper.selectByUsername(user.getUsername()) != null){
            map.put("usernameMessage","该账号存在");
            return map;
        }
//        if(userMapper.selectByEmail(user.getEmail()) != null){
//            map.put("mailMessage","该邮箱已存在");
//            return map;
//        }
        //注册用户
        user.setSalt(StudyAnalysisUtil.generateUUID().substring(0,5));
        user.setUserPassword(StudyAnalysisUtil.md5(user.getUserPassword() + user.getSalt()));
        user.setStatus(0);
        user.setUserType(type);
        userMapper.insertUser(user);
        return map;
    }
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    //登录
    public Map<String,Object> login(String username,String password,int expiredSeconds,int type){
        HashMap<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        User user = userMapper.selectByUsername(username);
        if(user == null){
            map.put("usernameMessage","该账号不存在");
            return map;
        }
        if(user.getUserType() != type){
            map.put("usernameMessage","账户类型错误");
            return map;
        }
//        if(user.getStatus() == 0){
//            map.put("usernameMessage","该账号未激活");
//            return map;
//        }
        String s = StudyAnalysisUtil.md5(password + user.getSalt());
        if(!user.getUserPassword().equals(s)){
            map.put("passwordMessage","密码错误");
            return map;
        }
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUsername(user.getUsername());
        loginTicket.setStatus(0);
        loginTicket.setUserId(user.getUserId());
        loginTicket.setTicket(StudyAnalysisUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public LoginTicket findLoginTicket(String ticket){
        // LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        //return loginTicket;
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        return loginTicket;
    }
    public User selectById(int id){
//        return userMapper.selectById(id);
        User user = getFromCache(id);
        if(user == null){
            user = setCache(id);
        }
        return user;
    }
    private User getFromCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user =(User) redisTemplate.opsForValue().get(redisKey);
        return user;
    }
    private User setCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
}
