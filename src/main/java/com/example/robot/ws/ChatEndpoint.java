package com.example.robot.ws;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.robot.config.HttpSessionConfigtor;
import com.example.robot.dao.Message;
import com.example.robot.dao.MessageList;
import com.example.robot.dao.User;
import com.example.robot.redis.mapper.MessageMapper;
import com.example.robot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chat/{loginId}" ,configurator=HttpSessionConfigtor.class)
public class ChatEndpoint {
    //存储客户端对象对应的ChatEndpoint对象
    private static Map<String,ChatEndpoint> onlineUsers=new ConcurrentHashMap<>();
    //声明session对象，该对象可以向指定用户发送信息
    private Session session;
    //声明httpsession对象，该对象存储了用户名
    private HttpSession httpSession;
    //从redis获取数据
    private static MessageMapper mapper;
    //从mysql获取数据
    private static UserService service;
    @Autowired
    public void setMapper(MessageMapper mapper) {
       ChatEndpoint.mapper=mapper;
    }
    @Autowired
    public void setService(UserService service) {
        ChatEndpoint.service = service;
    }

    /**
     * 初始化,用loginId查询消息并发送用户
     * @param session 发送信息的session,登陆者发送消息的对象
     * @param config  存储httpsession,即登陆者信息
     * @param loginId 登陆者id
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config,@PathParam("loginId") String loginId) throws IOException {
        this.session = session;
        this.httpSession=(HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        Set<String> queue = getMessageQueue(loginId);
        if(queue.size()==0) {
            return;
        }
        List<MessageList> msgList=new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ObjectMapper om=new ObjectMapper();

        for (String item : queue) {
            List<Message> msg=new ArrayList<>();
            MessageList messageList=new MessageList();
            String receiver="";
            List<MapRecord<String, Object, Object>> messagesRange = mapper.getMessagesRange(item);
            for (MapRecord<String, Object, Object> record : messagesRange) {
                Map<Object, Object> value = record.getValue();
                for (Map.Entry<Object, Object> entry : value.entrySet()) {
                    receiver = (String) entry.getKey();
                    Object v = entry.getValue();
                    msg.add(om.readValue((String) v, Message.class));
                }
            }
            messageList.setMsgList(msg);
            String rec = receiver.replace(loginId, "").replace(":", "");
            messageList.setReceiver(rec);
            QueryChainWrapper<User> query = service.query();
            List<User> account = query.eq("account", rec).list();
            if (account.size()==1){
                messageList.setSrc(account.get(0).getAvatar());
            }
            msgList.add(messageList);
        }
        String s = om.writeValueAsString(msgList);
        session.getBasicRemote().sendText(s);

        onlineUsers.put(loginId,this);
    }

    /**
     * 根据发送者和接受者的id进行消息传递
     * @param message 客户端传来的消息,包含通信双方的id
     * @param session 发送消息的对象
     */
    @OnMessage
    public void onMessage(String message,Session session) throws IOException {
        System.out.println(message);
        ObjectMapper om = new ObjectMapper();
        StringBuilder sb = new StringBuilder();
        Message msg =om.readValue(message,Message.class);
        String receiver = msg.getReceiver();
        System.out.println(msg);
        String str=msg.getSender().compareTo(msg.getReceiver())<0?msg.getSender()+":"+msg.getReceiver():msg.getReceiver()+":"+msg.getSender();
        mapper.xaddMessage(sb.append(MessageMapper.FRIENDMESSAGE).append(str).toString(),str,message);
        ChatEndpoint chatEndpoint = onlineUsers.get(receiver);
        chatEndpoint.session.getBasicRemote().sendText(message);
    }

    /**
     * 销毁注册对象
     * @param session 发送消息的对象
     * @param loginId 登陆者id
     */
    @OnClose
    public void onClose(Session session,@PathParam("loginId") String loginId){
        ChatEndpoint endpoint = onlineUsers.get(loginId);
        if (endpoint !=null){
            onlineUsers.remove(endpoint);
        }
    }

    /**
     * 广播
     * @param message 发送的消息
     */
    private void broadcastAllUsers(String message){
        try {
            for(Map.Entry<String,ChatEndpoint>pair : onlineUsers.entrySet()) {
                pair.getValue().session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getMessageQueue(String loginId){
        return mapper.getDialogIds(MessageMapper.DIALOGUEQUEUE+loginId);
    }

}
