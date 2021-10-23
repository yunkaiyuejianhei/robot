package com.example.robot.controller;

import com.example.robot.dao.Message;
import com.example.robot.redis.mapper.MessageMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import com.example.robot.dao.Result;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 主要用于测试接口效果
 */
@RestController
public class MessageContrller {
    @Autowired
    private MessageMapper mapper;
    @Autowired
    private StringRedisTemplate template;

    @GetMapping("/a/{mimeId}/{friendId}")
    public boolean addDialogId(@PathVariable String mimeId,@PathVariable String friendId){
        String id=mimeId.compareTo(friendId)<0?mimeId+":"+friendId:friendId+":"+mimeId;
        return mapper.addDialogId(MessageMapper.FRIENDMESSAGE+id,MessageMapper.DIALOGUEQUEUE+mimeId);
    }
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
        Message message = new Message("user","admin", "你好", "https://i.loli.net/2021/10/15/ardzkBqSEog2vU8.jpg",1);
        Message message1 = new Message("admin", "user","你好", "https://i.loli.net/2021/10/14/iH5w1T4Vf3CEjYl.jpg",1);
        String s = om.writeValueAsString(message);
        String s1 = om.writeValueAsString(message1);
        String str=id1.compareTo(id2)<0?id1+":"+id2:id2+":"+id1;
        System.out.println(s);
        System.out.println(s1);
        RecordId recordId = mapper.xaddMessage(MessageMapper.FRIENDMESSAGE + str, str, s);
        RecordId recordId1 = mapper.xaddMessage(MessageMapper.FRIENDMESSAGE + str, str, s1);
        System.out.println(recordId);
        System.out.println(recordId1);
//        addGroup(MessageMapper.FRIENDMESSAGE+str,id1,id2);
    }
    @GetMapping("group/{key}/{id1}/{id2}")
    public void addGroup(@PathVariable String key,@PathVariable String id1,@PathVariable String id2) {
        mapper.addGroup(key,MessageMapper.GROUPID+id1+":"+id2,MessageMapper.GROUPID+id2+":"+id1);
    }
    @GetMapping("/lua")
    public Object test() {
        List<String> keys=new ArrayList<>();
        keys.add("l1");
        Map<String,Object> map=new HashMap<>();
        Object execute = template.execute(RedisScript.of(new ClassPathResource("script/test.lua"),String.class),keys,"0","-1");
        System.out.println(execute);
        return execute;
    }


}
