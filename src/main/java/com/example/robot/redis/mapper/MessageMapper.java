package com.example.robot.redis.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class MessageMapper {
    /*组合个人id生成对话队列的id*/
    public static final String DIALOGUEQUEUE="DIALOGUEID:";
    /*组合双方好友id生成对话id*/
    public static final String FRIENDMESSAGE="FRIENDMESSAGEID:";
    /*消费组id*/
    public static final String GROUPID="GROUPID:";
    /*消费者id*/
    public static final String CONSUMERID="CONSUMERID:";
    /*聊天群组 消息记录列表id*/
    public static final String CHATGROUPLIST="CHATGROUPLIST:";
    /*聊天群组 成员组id*/
    public static final String CHATGROUP="CHATGROUPID:";
    @Autowired
    private ZSetOperations<String,String> zset;
    @Autowired
    private StreamOperations<String,Object,Object> stream;

    /**
     * 用zset存会话列表 如果key存在，则添加失败
     * @param dialogId  对话id
     * @param key DIALOGUEID: + 个人id
     */
    public boolean addDialogId(String dialogId, String key){
        boolean aBoolean = zset.addIfAbsent(key, dialogId, 0);
        if (aBoolean){
            log.info("会话添加成功");
        } else{
            log.warn("会话已存在");
        }
        return aBoolean;
    }

    /**
     * 获取当前用户所有会话列表id
     * @param key DIALOGUEID: + 个人id
     * @return
     */
    public Set<String> getDialogIds(String key){
        return zset.range(key,0,-1);
    }

    /**
     * 用stream存储好友双方的消息列表,最多存储300条历史记录
     * @param key 对话id
     * @param file  发送人id/接收人id
     * @param message 消息内容
     */
    public RecordId xaddMessage(String key, String file,String message){
        return stream.add(key, Collections.singletonMap(file, message));
    }

    /**
     * 创建两个消费组
     * @param key       DIALOGUEID: + 个人id
     * @param group1    GROUPID: + 个人id
     * @param group2    GROUPID: + 收件人id
     */
    public void addGroup(String key,String group1,String group2){
        stream.createGroup(key, group1);
        stream.createGroup(key, group2);
    }

    /**
     * 消费者消费消息,每次消费100条，不足则阻塞1秒
     * @param key       DIALOGUEID: + 个人id
     * @param group     GROUPID: + 个人id
     * @param consumer  CONSUMERID: + 个人id
     * @return
     */

    public List<MapRecord<String, Object, Object>> readConsumer(String key, String group, String consumer){
        return stream.read(Consumer.from(group,consumer),
                StreamReadOptions.empty().count(100).block(Duration.ofMillis(1000)),
                StreamOffset.create(key, ReadOffset.lastConsumed()));
    }

    /**
     * 获取消息记录 从新到旧
     * XREVRANGE key end (-) start (+)  COUNT count
     * @param key
     * @return
     */
    public List<MapRecord<String, Object, Object>> getMessagesRange(String key){
        return stream.range(key, Range.open("-", "+"), RedisZSetCommands.Limit.limit().count(100));
    }

    /**
     * 创建聊天群组
     * @return
     */
    public RecordId createChatGroup(String groupName, String file,List<String> members){
        members.stream().forEach(item->zset.addIfAbsent(groupName,item,0));
        return xaddMessage(groupName,file,groupName+" 创建成功。");
    }

}
