package com.example.robot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

@Configuration
public class RedisConfig {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 提供ZSet 可排序的set
     * @return
     */
    @Bean
    public ZSetOperations<String, String> getZSet(){
         return stringRedisTemplate.opsForZSet();
    }

    /**
     * 提供List双端列表
     * @return
     */
    @Bean
    public ListOperations<String, String> getList(){
        return stringRedisTemplate.opsForList();
    }

    /**
     * 适合做消息队列
     * @return
     */
    @Bean
    public StreamOperations<String,Object,Object>  getStream(){
        return stringRedisTemplate.opsForStream();
    }

    @Bean
    public DefaultRedisScript<List> getScript(){
        DefaultRedisScript<List> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(List.class);
        ClassPathResource classPathResource = new ClassPathResource("script/test.lua");
//        if (!classPathResource.exists()){
//            classPathResource.createRelative("src/main/java/com/example/robot/script/test.lua");
//        }
        defaultRedisScript.setScriptSource(new ResourceScriptSource(classPathResource));
        return defaultRedisScript;
    }

}
