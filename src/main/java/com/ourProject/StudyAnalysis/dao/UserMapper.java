package com.ourProject.StudyAnalysis.dao;

import com.ourProject.StudyAnalysis.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User selectByUsername(String username);
    User selectById(int id);
}
