package com.wangwei.config;

import com.wangwei.websocket.AdminWebSocketServer;
import com.wangwei.websocket.UserWebSocketServer;

import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {


    private final UserWebSocketServer userWebSocketServer;
    private final AdminWebSocketServer adminWebSocketServer;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {


        // 用户端
        registry.addHandler(userWebSocketServer, "/ws/user")
                .setAllowedOrigins("*");

        // 管理端
        registry.addHandler(adminWebSocketServer, "/ws/admin")
                .setAllowedOrigins("*");
    }
}

