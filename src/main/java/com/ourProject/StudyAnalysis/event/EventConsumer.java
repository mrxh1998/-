package com.ourProject.StudyAnalysis.event;

import com.alibaba.fastjson.JSONObject;
import com.ourProject.StudyAnalysis.entity.Event;
import com.ourProject.StudyAnalysis.service.ClassService;
import com.ourProject.StudyAnalysis.service.StudyService;
import com.ourProject.StudyAnalysis.util.AnalysisConstant;
import com.ourProject.StudyAnalysis.util.StudyAnalysisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements AnalysisConstant {
    @Autowired
    StudyService studyService;
    @Autowired
    ClassService classService;
    @KafkaListener(topics = {TOPIC_STUDY})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value()==null){
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            return;
        }
        studyService.addStudyTime(event.getStudentId(),event.getClassId());
    }
    @KafkaListener(topics = {TOPIC_WARNING})
    public void handleWarningMessage(ConsumerRecord record){
        if(record == null || record.value()==null){
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            return;
        }
        studyService.warning(event.getClassId(),event.getStudentId());
    }
    @KafkaListener(topics = {TOPIC_MESSAGE})
    public void sendMessages(ConsumerRecord record){
        if(record == null || record.value()==null){
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            return;
        }
        classService.sendMessage(event.getClassId(),(String) event.getMap().get("content"));
    }
}
