package com.example.robot.dao;

import lombok.Data;

@Data
public class ResultMessage {
    private boolean isSystem;//是否系统消息
    private String receiver;//收件人
    private Object message;//消息
}
