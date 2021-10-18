package com.example.robot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Configuration
public class Interceptor implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                boolean b = request.getSession().getAttributeNames().hasMoreElements();
                if (!b) {
                    request.getRequestDispatcher("/login").forward(request,response);
                }
                return b;
            }
        }).addPathPatterns("/**").excludePathPatterns("/login","/user/*","/chat/*","/css/**","/js/**","/fonts/**");
    }
}
