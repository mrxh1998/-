package com.ourProject.StudyAnalysis.service;

import com.ourProject.StudyAnalysis.dao.ClassMapper;
import com.ourProject.StudyAnalysis.dao.UserMapper;
import com.ourProject.StudyAnalysis.entity.Class;
import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.util.HostHolder;
import com.ourProject.StudyAnalysis.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClassService {
    @Autowired
    ClassMapper classMapper;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;
    public Map<String,String> createClass(String className){
        Map<String,String> map = new HashMap<>();
        User teacher = hostHolder.getUser();
        List<Class> classes = classMapper.selectByName(className);
        if( classes.size() != 0){
            for(Class _class : classes){
                if(_class.getTeacherId() == teacher.getUserId()){
                    map.put("classNameMsg","您已经创建过同名课堂了");
                    return map;
                }
            }
        }
        Class bClass = new Class();
        bClass.setClassName(className);
        bClass.setTeacherId(teacher.getUserId());
        classMapper.insertClass(bClass);
        return map;
    }
    public List<Class> searchClass(String className){
        List<Class> classes = classMapper.selectByName(className);

        return classes;
    }
    public void joinClass(int classId){
        User user = hostHolder.getUser();
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String classKey = RedisKeyUtil.getFollower(classId);
                String studentKey = RedisKeyUtil.getFollowee(user.getUserId());
                operations.multi();
                operations.opsForSet().add(classKey,user.getUserId());
                operations.opsForSet().add(studentKey,classId);
                return operations.exec();
            }
        });

    }
    public List<Class> findTeacherClasses(){
        User user = hostHolder.getUser();
        List<Class> classes = classMapper.selectByTeacherId(user.getUserId(), 0, 1000);
        return classes;
    }
    public List<Class> findStudentClasses(int userId){
        Set<Integer> members = redisTemplate.opsForSet().members(RedisKeyUtil.getFollowee(userId));
        List<Class> classes = new ArrayList<>();
        for(Integer integer : members){
            classes.add(classMapper.selectById(integer));
        }
        return classes;
    }
    public Class findClassById(int classId){
        return classMapper.selectById(classId);
    }
    public List<String> findFile(int classId){
        String key = RedisKeyUtil.getClassFileKey(classId);
        Long size = redisTemplate.opsForList().size(key);
        List<String> range = redisTemplate.opsForList().range(key, 0, size);
        return range;
    }
    public List<User> findStudents(int classId){
        String key = RedisKeyUtil.getFollower(classId);
        Set<Integer> members = redisTemplate.opsForSet().members(key);
        List<User> students = new ArrayList<>();
        for(Integer integer : members){
            User user = userMapper.selectById(integer);
            students.add(user);
        }
        return students;
    }
    public int findStudentNumber(int classId){
        String key = RedisKeyUtil.getFollower(classId);
        Set<Integer> members = redisTemplate.opsForSet().members(key);
        if(members == null){
            return 0;
        }
        else{
            return members.size();
        }
    }
    public void sendMessage(int classId,String content){
        String key = RedisKeyUtil.getMessageKey(classId);
        redisTemplate.opsForList().leftPush(key,content);
    }
    public List<String> findMessages(int classId){
        String key = RedisKeyUtil.getMessageKey(classId);
        List<String> range = redisTemplate.opsForList().range(key, 0, redisTemplate.opsForList().size(key));
        return range;
    }
}
