package com.ourProject.StudyAnalysis.dao;

import com.ourProject.StudyAnalysis.entity.Class;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
@Mapper
public interface ClassMapper {
    Class selectById(int classId);
    List<Class> selectByName(String className);
    List<Class> selectByTeacherId(int teacherId, int offset, int limit);
    int insertClass(Class _class);
}
