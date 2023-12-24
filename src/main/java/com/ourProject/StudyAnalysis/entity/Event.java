package com.ourProject.StudyAnalysis.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    String topic;
    int classId;
    int studentId;

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", classId=" + classId +
                ", studentId=" + studentId +
                ", map=" + map +
                '}';
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(String key,Object value) {
        this.map.put(key,value);
    }

    Map<String,Object> map = new HashMap<>();
}
