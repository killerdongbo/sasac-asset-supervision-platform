package com.sasac.platform.notification.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        if (userId != null) {
            sessions.put(userId, session);
            log.info("WS connected: userId={}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            log.info("WS disconnected: userId={}", userId);
        }
    }

    public void push(Long userId, Object message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.warn("WS push failed: userId={}, err={}", userId, e.getMessage());
            }
        }
    }

    public void broadcast(Object message) {
        for (Map.Entry<Long, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getValue().isOpen()) {
                try {
                    String json = mapper.writeValueAsString(message);
                    entry.getValue().sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    log.warn("WS broadcast failed: userId={}", entry.getKey());
                }
            }
        }
    }

    private Long getUserId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2 && "userId".equals(kv[0])) {
                    try { return Long.parseLong(kv[1]); } catch (NumberFormatException e) { return null; }
                }
            }
        }
        return null;
    }
}
