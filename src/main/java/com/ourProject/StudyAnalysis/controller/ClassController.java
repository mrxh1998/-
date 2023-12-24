package com.ourProject.StudyAnalysis.controller;

import com.ourProject.StudyAnalysis.entity.Class;
import com.ourProject.StudyAnalysis.entity.Event;
import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.event.EventProducer;
import com.ourProject.StudyAnalysis.service.ClassService;
import com.ourProject.StudyAnalysis.service.UserService;
import com.ourProject.StudyAnalysis.util.AnalysisConstant;
import com.ourProject.StudyAnalysis.util.HostHolder;
import com.ourProject.StudyAnalysis.util.RedisKeyUtil;
import com.ourProject.StudyAnalysis.util.StudyAnalysisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class ClassController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    ClassService classService;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = "/createClass",method = RequestMethod.GET)
    public String getCreateClassPage(){
        return "createClass";
    }
    @RequestMapping(path = "/createClass",method = RequestMethod.POST)
    public String createClass(String courseName, Model model){
        User teacher = hostHolder.getUser();
        Map<String, String> map = classService.createClass(courseName);
        if(map.size() == 0){
            return "redirect:/index";
        }
        model.addAttribute("classNameMsg",map.get("classNameMsg"));
        return "/createClass";
    }
    @RequestMapping(path = "/searchClass",method = RequestMethod.POST)
    public String searchClass(String className,Model model){
        List<Class> classes = classService.searchClass(className);
        User student = hostHolder.getUser();
        List<Map<String,Object>> classList = new ArrayList<>();
        if(classes != null){
            for(Class _class : classes){
                Map<String,Object>map = new HashMap<>();
                Boolean member = redisTemplate.opsForSet().isMember(RedisKeyUtil.getFollowee(student.getUserId()),_class.getId());
                map.put("isMember",member);
                map.put("class",_class);
                User user = userService.selectById(_class.getTeacherId());
                map.put("teacher",user);
                classList.add(map);
            }
            model.addAttribute("classList",classList);
        }
        return "/search";
    }
    @RequestMapping(path="/joinClass",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int classId){
        User user = hostHolder.getUser();
        classService.joinClass(classId);
        //触发关注事件
        return StudyAnalysisUtil.getJSONString(0,"已加入");
    }
    @RequestMapping(path = "/Classes",method = RequestMethod.GET)
    public String getTeacherClassesPage(Model model){
        User user1 = hostHolder.getUser();
        List<Class> classes;
        if(user1.getUserType() == 1){
            classes = classService.findStudentClasses(user1.getUserId());
        }
        else{
            classes = classService.findTeacherClasses();
        }
        List<Map<String,Object>> classList = new ArrayList<>();
        if(classes != null){
            for(Class _class : classes){
                Map<String,Object>map = new HashMap<>();
                map.put("class",_class);
                User user = userService.selectById(_class.getTeacherId());
                map.put("teacher",user);
                classList.add(map);
            }
            model.addAttribute("classList",classList);
        }
        return "/mycourse";
    }
    @RequestMapping(path = "/classIndex/{classId}",method = RequestMethod.GET)
    public String getClassIndex(@PathVariable(name = "classId")int classId,Model model){
        Class classById = classService.findClassById(classId);
        User teacher = userService.selectById(classById.getTeacherId());
        List<String> file = classService.findFile(classId);
        List<Map<String,Object>> list = new ArrayList<>();
        if(file != null){
            for(String str : file){
                Map<String ,Object> map = new HashMap<>();
                map.put("filepath",str);
                String substring = str.substring(str.lastIndexOf('/')+1, str.lastIndexOf('.'));
                map.put("filename",substring);
                list.add(map);
            }
        }
        model.addAttribute("class",classById);
        model.addAttribute("teacher",teacher);
        model.addAttribute("fileList",list);
        return "/courseindex";
    }
    @RequestMapping(path = "/inform/{classId}",method = RequestMethod.GET)
    public String getInformPage(@PathVariable(name = "classId")int classId,Model model){
        Class classById = classService.findClassById(classId);
        model.addAttribute("class",classById);
        List<String> messages = classService.findMessages(classId);
        model.addAttribute("messages",messages);
        return "/inform";
    }
    @RequestMapping(path = "/inform/{classId}",method = RequestMethod.POST)
    public String inform(@PathVariable(name = "classId")int classId,String context){
        Event event = new Event();
        event.setClassId(classId);
        event.setTopic(AnalysisConstant.TOPIC_MESSAGE);
        event.setMap("content",context);
        eventProducer.fireEvent(event);
        return "redirect:/index";
    }
}
