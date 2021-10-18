package com.example.robot.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;//发送者
    private String receiver;//接收者
    private String message;//消息
    private String src;//发送者图像路径
}
