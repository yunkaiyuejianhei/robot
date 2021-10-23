package com.example.robot.controller;

import com.example.robot.dao.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
public class ChatController {
    @GetMapping("/login")
    public String main(){
        return "login";
    }

    @GetMapping("/chat/{loginId}")
    public String chat(@PathVariable String loginId, HttpSession session, Model model){
        User attribute = (User) session.getAttribute(loginId);
        System.out.println(attribute);
        model.addAttribute("loginId",attribute);
        return "user/chat";
    }
}
