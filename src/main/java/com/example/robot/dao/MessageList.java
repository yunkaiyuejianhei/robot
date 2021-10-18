package com.example.robot.dao;

import lombok.Data;

import java.util.List;

@Data
public class MessageList{
    private String receiver;//收件人
    private String src;//收件人头像路径
    private List<Message> msgList;//对话消息列表{
}
