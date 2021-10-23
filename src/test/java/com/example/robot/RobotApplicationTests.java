package com.example.robot;

import com.example.robot.dao.User;
import com.example.robot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;

@SpringBootTest
class RobotApplicationTests {
    @Autowired
    StringRedisTemplate template;
    @Test
    void contextLoads(){

    }

}
