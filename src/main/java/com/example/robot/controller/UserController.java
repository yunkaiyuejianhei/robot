package com.example.robot.controller;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.robot.dao.Result;
import com.example.robot.dao.User;
import com.example.robot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/main")
    public Result login(User user, HttpSession session){
        Result result = new Result();
        QueryChainWrapper<User> query = service.query();
        List<User> account = query.eq("account", user.getAccount()).list();
        if (account.size()==1){
            User u = account.get(0);
            if (u.getAccount().equals(user.getAccount())&&u.getPassword().equals(user.getPassword())){
                session.setAttribute(u.getAccount(),u);
                result.setFlag(true);
                result.setMessage(u.getAccount());
                return result;
            }
        }
        result.setFlag(false);
        result.setMessage("账户或密码错误");
        return  result;
    }
    @PostMapping("/register")
    public Result register(User user){
        Result result=new Result();
        if (user!=null){
            user.setAvatar("https://i.loli.net/2021/10/15/ardzkBqSEog2vU8.jpg");
            boolean save = service.save(user);
            if (save) {
                result.setFlag(true);
                result.setMessage("注册成功，3秒后跳转");
            }
        }
        result.setFlag(false);
        result.setMessage("注册失败");
        return result;
    }
}
