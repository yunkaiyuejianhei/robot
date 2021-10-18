package com.example.robot.utils;

import com.example.robot.dao.ResultMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketUtils {
    private WebSocketUtils(){}

    /**
     * 将消息封装
     * @param isSystemMessage 是否为系统消息
     * @param receiver  收件人
     * @param message   消息
     * @return
     */
    public static String getMessage(boolean isSystemMessage,String receiver,Object message){
            ResultMessage result=new ResultMessage();
            result.setSystem(isSystemMessage);
            result.setReceiver(receiver);
            result.setMessage(message);
            ObjectMapper om=new ObjectMapper();
            try {
                    return om.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
    }
}
