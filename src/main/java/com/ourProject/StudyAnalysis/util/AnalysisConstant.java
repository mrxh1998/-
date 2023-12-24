package com.ourProject.StudyAnalysis.util;

public interface AnalysisConstant {
    int DEFAULT_EXPIRED_SECONDS = 3600*12; //默认状态超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100; //记住我的事件
    String TOPIC_STUDY = "study";
    String TOPIC_MESSAGE = "message";
    String TOPIC_WARNING = "warning";
}
