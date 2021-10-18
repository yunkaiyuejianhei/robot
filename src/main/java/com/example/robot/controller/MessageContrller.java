package com.example.robot.controller;

import com.example.robot.dao.Message;
import com.example.robot.redis.mapper.MessageMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class MessageContrller {
    @Autowired
    private MessageMapper mapper;

    @GetMapping("/d/{key}")
    public Set<String> getDialogIds(@PathVariable String key){
        return mapper.getDialogIds(key);
    }

    @GetMapping("/xadd/{key}/{file}/{message}")
    public void xaddMessage(@PathVariable String key,@PathVariable String file,@PathVariable String message){
        mapper.xaddMessage(key, file, message);
    }

    @GetMapping("/read/{key}/{group}/{consumer}")
    public List<MapRecord<String, Object, Object>> readConsumer(@PathVariable String key,
                                                                @PathVariable String group, @PathVariable String consumer) {
        return mapper.readConsumer(key, group, consumer);
    }

    @GetMapping("/test/{id1}/{id2}")
    public void main1(@PathVariable String id1,@PathVariable String id2) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Message message = new Message("user","admin", "你好", "https://i.loli.net/2021/10/15/ardzkBqSEog2vU8.jpg");
        Message message1 = new Message("admin", "user","你好", "https://i.loli.net/2021/10/14/iH5w1T4Vf3CEjYl.jpg");
        String s = om.writeValueAsString(message);
        String s1 = om.writeValueAsString(message1);
        String str=id1.compareTo(id2)<0?id1+":"+id2:id2+":"+id1;
        mapper.xaddMessage(MessageMapper.FRIENDMESSAGE+str,str,s);
        mapper.xaddMessage(MessageMapper.FRIENDMESSAGE+str,str,s1);
        addGroup(MessageMapper.FRIENDMESSAGE+str,id1,id2);
    }
    @GetMapping("group/{key}/{id1}/{id2}")
    public void addGroup(@PathVariable String key,@PathVariable String id1,@PathVariable String id2) {
        mapper.addGroup(key,MessageMapper.GROUPID+id1+":"+id2,MessageMapper.GROUPID+id2+":"+id1);
    }
}
