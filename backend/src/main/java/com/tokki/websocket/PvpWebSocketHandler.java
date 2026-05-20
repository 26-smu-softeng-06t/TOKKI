package com.tokki.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PvpWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket connected: {}", session.getId());

        String roomId = getRoomId(session);
        if (roomId != null) {
            joinRoom(session, Long.parseLong(roomId));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");
            Long roomId = ((Number) payload.get("roomId")).longValue();

            switch (type) {
                case "JOIN_ROOM":
                    joinRoom(session, roomId);
                    broadcastRoomState(roomId);
                    break;
                case "PROGRESS":
                    broadcastToRoom(roomId, payload);
                    break;
                case "BATTLE_COMPLETE":
                    broadcastToRoom(roomId, payload);
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
            }
        } catch (Exception e) {
            log.error("Error handling message: {}", message.getPayload(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        String roomId = getRoomId(session);
        if (roomId != null) {
            leaveRoom(session, Long.parseLong(roomId));
        }
        log.info("WebSocket disconnected: {} ({})", session.getId(), status);
    }

    private String getRoomId(WebSocketSession session) {
        String uri = session.getUri().toString();
        if (uri.contains("roomId=")) {
            return uri.substring(uri.indexOf("roomId=") + 7).split("&")[0];
        }
        return null;
    }

    private void joinRoom(WebSocketSession session, Long roomId) {
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        log.info("Session {} joined room {}", session.getId(), roomId);
    }

    private void leaveRoom(WebSocketSession session, Long roomId) {
        ConcurrentHashMap<String, WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            room.remove(session.getId());
            if (room.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }

    private void broadcastToRoom(Long roomId, Object message) {
        ConcurrentHashMap<String, WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            room.forEach((id, session) -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                    }
                } catch (IOException e) {
                    log.error("Error broadcasting to session {}: {}", id, e.getMessage());
                }
            });
        }
    }

    private void broadcastRoomState(Long roomId) {
        ConcurrentHashMap<String, WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            Map<String, Object> state = Map.of(
                "type", "ROOM_STATE",
                "roomId", roomId,
                "playerCount", room.size()
            );
            broadcastToRoom(roomId, state);
        }
    }
}
