package com.ourProject.StudyAnalysis.controller;

import com.ourProject.StudyAnalysis.entity.Class;
import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.entity.Warning;
import com.ourProject.StudyAnalysis.service.ClassService;
import com.ourProject.StudyAnalysis.service.StudyService;
import com.ourProject.StudyAnalysis.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserHomeController {
    @Autowired
    StudyService studyService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    ClassService classService;
    @RequestMapping(path = "/userDetail",method = RequestMethod.GET)
    public String getHomePage(Model model){
        User user = hostHolder.getUser();
        if(user.getUserType()==1){
            List<Warning> warnings = studyService.findWarning(user.getUserId());
            List<Map<String,Object>> warningList = new ArrayList<>();
            List<Class> studentClasses = classService.findStudentClasses(user.getUserId());
            List<Map<String ,Object>> messageList = new ArrayList<>();
            if(studentClasses != null){
                for(Class _class: studentClasses){
                    Map<String,Object> map = new HashMap<>();
                    List<String> messages = classService.findMessages(_class.getId());
                    if( messages != null && messages.size() != 0){
                        map.put("class",_class);
                        messageList.add(map);
                    }
                }
            }
            model.addAttribute("messageList",messageList);
            if(warnings != null){
                for(Warning warning : warnings){
                    Map<String,Object>map = new HashMap<>();
                    map.put("warning",warning);
                    Class classById = classService.findClassById(warning.getClassId());
                    map.put("class",classById);
                    warningList.add(map);
                }
            }
            model.addAttribute("warningList",warningList);
        }
        return "personal";
    }
}
