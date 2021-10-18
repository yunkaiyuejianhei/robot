package com.example.robot;

import com.example.robot.dao.User;
import com.example.robot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RobotApplicationTests {
    @Autowired
    UserService service;
    @Test
    void contextLoads() {

    }

}
