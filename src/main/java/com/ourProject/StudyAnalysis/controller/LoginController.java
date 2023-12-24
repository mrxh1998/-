package com.ourProject.StudyAnalysis.controller;

import com.ourProject.StudyAnalysis.entity.User;
import com.ourProject.StudyAnalysis.service.UserService;
import com.ourProject.StudyAnalysis.util.AnalysisConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController implements AnalysisConstant {
    @Autowired
    UserService userService;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/login";
    }
    @RequestMapping(path = "/tlogin",method = RequestMethod.GET)
    public String getTloginPage(){
        return "/tlogin";
    }

    @RequestMapping(path = "/register/{type}",method = RequestMethod.POST)
    public String register(Model model, User user, @PathVariable(name = "type")int type){
        Map<String, Object> register = userService.addUser(user, type);
        if(register == null || register.isEmpty()){
            model.addAttribute("msg","注册成功");
            model.addAttribute("target","/index");
            return "operate-result";
        }
        else{
            model.addAttribute("msg",(String)register.get("usernameMessage") + (String)register.get("passwordMessage"));
            model.addAttribute("target","/login");
            return "operate-result";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/index";
    }
    @RequestMapping(path="/login/{type}",method = RequestMethod.POST)
    public String login(Model model, String username, String password, HttpServletResponse response,
            boolean rememberMe,@PathVariable(name = "type") int type){
        //检查账号密码
        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> login = userService.login(username, password ,expiredSeconds,type);
        if(login.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",(String)login.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        else{
            model.addAttribute("usernameMsg",login.get("usernameMessage"));
            model.addAttribute("passwordMsg",login.get("passwordMessage"));
            return "/login";
        }
    }
}
