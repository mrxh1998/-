package com.ourProject.StudyAnalysis.controller;

import com.ourProject.StudyAnalysis.entity.Class;
import com.ourProject.StudyAnalysis.entity.Event;
import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.event.EventProducer;
import com.ourProject.StudyAnalysis.service.ClassService;
import com.ourProject.StudyAnalysis.service.StudyService;
import com.ourProject.StudyAnalysis.util.AnalysisConstant;
import com.ourProject.StudyAnalysis.util.HostHolder;
import com.ourProject.StudyAnalysis.util.StudyAnalysisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StudyController {
    @Value("${StudyAnalysis.path.domain}")
    private String domain;
    @Value("${StudyAnalysis.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    StudyService studyService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    ClassService classService;
    @RequestMapping(path = "/upload/{classId}",method = RequestMethod.GET)
    public String getUploadPage(@PathVariable(name = "classId")int classId,Model model){
        model.addAttribute("classId",classId);
        return "/upvideo";
    }
    @RequestMapping(path = "/getFile/{filename}",method = RequestMethod.POST)
    public void getFile(@PathVariable(name = "filename")String filename){

    }
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String upload(MultipartFile file, Model model,String _classId){
        int classId = Integer.parseInt(_classId);
        if(file == null){
            model.addAttribute("error","您还没有选择文件");
            return "/upvideo";
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确");
            return "/upvideo";
        }
        String filename = originalFilename;
        File dest = new File(uploadPath+"/"+classId+"/"+filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }
        //http:localhost:8080/StudyAnalysis/{classId}/xxx.png
        String header = domain + contextPath  +"/" + classId + "/"+filename;
        studyService.updateFile(classId,header);
        return "/index";
    }
    @RequestMapping(path = "/study/{classId}/{filename}",method = RequestMethod.GET)
    public String getStudyPage(@PathVariable(name = "filename")String filename,Model model,
                               @PathVariable(name="classId")int classId){
        model.addAttribute("classId",classId);
        model.addAttribute("filename",filename);
        return "/study";
    }
    @RequestMapping(path = "/video/{classId}/{filename}",method = RequestMethod.GET)
    public void getVideo(@PathVariable(name="classId")int classId,@PathVariable(name = "filename")String filename,
                         HttpServletResponse response){
        filename = uploadPath + "/"+classId+"/"+filename;
        //声明文件格式
        String suffix = filename.substring(filename.lastIndexOf('.'));
        response.setContentType("video/"+suffix);
        try(
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename);
        ) {
            byte[]buffer = new byte[1024];
            int b = 0;
            while((b=fileInputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
        }
    }
    @RequestMapping(path = "/study",method = RequestMethod.POST)
    @ResponseBody
    public String study(int classId){
        User student = hostHolder.getUser();
        Event event = new Event();
        event.setClassId(classId);
        event.setStudentId(student.getUserId());
        event.setTopic(AnalysisConstant.TOPIC_STUDY);
        eventProducer.fireEvent(event);
        return StudyAnalysisUtil.getJSONString(0,"添加成功");
    }
    @RequestMapping(path = "/studyCase/{classId}",method = RequestMethod.GET)
    public String getStudyCasePage(@PathVariable(name = "classId")int classId,Model model){
        List<User> students = classService.findStudents(classId);
        List<Map<String,Object>> studentList = new ArrayList<>();
        if(students != null){
            for(User student:students){
                Map<String,Object> map = new HashMap<>();
                map.put("student",student);
                int allStudyTime = studyService.findAllStudyTime(classId, student.getUserId());
                map.put("studyTime",allStudyTime);
                studentList.add(map);
            }
        }
        Class classById = classService.findClassById(classId);
        model.addAttribute("studentNumber",classService.findStudentNumber(classId));
        model.addAttribute("class",classById);
        model.addAttribute("studentList",studentList);
        return "/studycase";
    }
    @RequestMapping(path = "/warning",method = RequestMethod.POST)
    @ResponseBody
    public String warning(int classId,int studentId){
        Event event = new Event();
        event.setClassId(classId);
        event.setStudentId(studentId);
        event.setTopic(AnalysisConstant.TOPIC_WARNING);
        eventProducer.fireEvent(event);
        return StudyAnalysisUtil.getJSONString(0,"通知成功");
    }
    @RequestMapping(path="/studentStudyCase/{userId}/{classId}",method = RequestMethod.GET)
    public String getStudentStudyCasePage(@PathVariable(name = "userId")int userId,
                                          @PathVariable(name = "classId")int classId,Model model){
        Class classById = classService.findClassById(classId);
        model.addAttribute("class",classById);
        int allStudyTime = studyService.findAllStudyTime(classId, userId);
        model.addAttribute("allStudyTime",allStudyTime);
        List<Integer> nowMonthDayList = StudyAnalysisUtil.getNowMonthDayList();
        model.addAttribute("dayList",nowMonthDayList);
        int month = StudyAnalysisUtil.getNowMonth();
        List<Integer> studyTimeList = new ArrayList<>();
        for(Integer i : nowMonthDayList){
            int studyTimeByMonthByDay = studyService.findStudyTimeByMonthByDay(month, i, classId, userId);
            studyTimeList.add(studyTimeByMonthByDay);
        }
        model.addAttribute("studyTimeList",studyTimeList);
        return "/studycase1";
    }
}
