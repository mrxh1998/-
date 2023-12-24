package com.ourProject.StudyAnalysis.entity;

import java.util.Date;

public class Warning {
    Date date = new Date();
    int classId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    String msg;

    @Override
    public String toString() {
        return "Warning{" +
                "date=" + date +
                ", classId=" + classId +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
