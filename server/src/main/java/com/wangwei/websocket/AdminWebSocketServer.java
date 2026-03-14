package com.wangwei.websocket;

import com.wangwei.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminWebSocketServer extends TextWebSocketHandler {

    private static final String LOGIN_USER_KEY = "login:user:";

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    // sid -> session
    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
        Integer role = (Integer) claims.get("role");
        if (role != 1) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        // redis 验证 token
        String redisToken = (String) redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);
        if (redisToken == null || !redisToken.equals(token)) {
            log.info("token 验证失败：{}", userId);
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        log.info("WebSocket 连接成功admin：{}", userId);
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

    // 单发
    public void sendToOne(String sid, String msg) throws Exception {
        WebSocketSession session = SESSION_MAP.get(sid);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(msg));
        }
    }

    // 群发
    public void sendToAll(String msg) throws Exception {
        for (WebSocketSession session : SESSION_MAP.values()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(msg));
            }
        }
    }

    private String getToken(String query) {
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if ("token" .equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}
