package com.wangwei.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangwei.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserWebSocketServer extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private static final String LOGIN_USER_KEY = "login:student:";
    private final JwtUtils jwtUtils;
    // sid -> session
    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        // 解析 token 获取userId
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        log.info("query: {}", query);
        String token = getToken(query);

        if (token == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        Claims claims = jwtUtils.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        // 判断role 是否为 0 -> 学生
        Integer role = (Integer) claims.get("role");
        if (role != 0) {
            log.warn("角色校验不通过，预期为0，实际为: {}", role);
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        // redis 验证 token
        String redisToken = (String) redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);
        log.info("redisToken: {}", redisToken);
        if (redisToken == null || !redisToken.equals(token)) {
            log.info("token 验证失败：{}", userId);
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        log.info("WebSocket 连接成功user：{}", userId);
        SESSION_MAP.put(String.valueOf(userId), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("收到消息：{}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SESSION_MAP.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket 连接断开：{}", session.getId());
    }

    // 发送对象消息（自动转 JSON）
    public void sendJson(String sid, Object obj) {
        WebSocketSession session = SESSION_MAP.get(sid);
        try {
            if (session != null && session.isOpen()) {
                // 将对象转为 JSON 字符串
                String json = objectMapper.writeValueAsString(obj);
                session.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            log.error("发送 WebSocket 消息失败，userId: {}", sid, e);
        }
    }

    // 批量发送对象
    public void sendJsonToAll(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            TextMessage textMessage = new TextMessage(json);
            for (WebSocketSession session : SESSION_MAP.values()) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        } catch (Exception e) {
            log.error("群发消息失败", e);
        }
    }

    private String getToken(String query) {
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if ("token".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}


