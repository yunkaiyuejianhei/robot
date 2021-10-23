package com.example.robot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.EndpointConfig;

@Configuration
public class WebSocketConfig {

    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
