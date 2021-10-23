package com.example.robot.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     *发送者
     */
    private String sender;
    /**
     *  接收者 */
    private String receiver;
    /**
     *消息
     */
    private String message;
    /**
     * 发送者图像路径
     */
    private String src;
    /**
     * 消息类型
     */
    private int typeInfo;
}
