package com.ourProject.StudyAnalysis.event;

import com.alibaba.fastjson.JSONObject;
import com.ourProject.StudyAnalysis.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    KafkaTemplate kafkaTemplate;
    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
