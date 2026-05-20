package com.tokki.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
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
    private final ConcurrentHashMap<Long, RoomState> roomStates = new ConcurrentHashMap<>();

    @Data
    static class PlayerResult {
        private String userId;
        private int score;
        private int time;
    }

    @Data
    static class RoomState {
        private final ConcurrentHashMap<String, PlayerResult> results = new ConcurrentHashMap<>();
        private final Object lock = new Object();
    }

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
                    handleBattleComplete(session, roomId, payload);
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
        roomStates.computeIfAbsent(roomId, k -> new RoomState());
        log.info("Session {} joined room {}", session.getId(), roomId);
    }

    private void leaveRoom(WebSocketSession session, Long roomId) {
        ConcurrentHashMap<String, WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            room.remove(session.getId());
            if (room.isEmpty()) {
                roomSessions.remove(roomId);
                roomStates.remove(roomId);
            }
        }
    }

    private void handleBattleComplete(WebSocketSession session, Long roomId, Map<String, Object> payload) {
        RoomState roomState = roomStates.get(roomId);
        if (roomState == null) {
            log.warn("Room state not found for room: {}", roomId);
            return;
        }

        synchronized (roomState.lock) {
            String userId = (String) payload.get("winnerId"); // sender's userId
            int score = ((Number) payload.get("hostScore")).intValue();
            int time = ((Number) payload.get("hostTime")).intValue();

            PlayerResult result = new PlayerResult();
            result.setUserId(userId);
            result.setScore(score);
            result.setTime(time);
            roomState.getResults().put(session.getId(), result);

            // Check if both players have submitted
            if (roomState.getResults().size() >= 2) {
                // Determine winner
                var results = roomState.getResults().values().toArray(new PlayerResult[0]);
                PlayerResult r1 = results[0];
                PlayerResult r2 = results[1];

                String winnerId;
                if (r1.getScore() > r2.getScore()) {
                    winnerId = r1.getUserId();
                } else if (r2.getScore() > r1.getScore()) {
                    winnerId = r2.getUserId();
                } else {
                    // Tie breaker: shorter time wins
                    winnerId = r1.getTime() <= r2.getTime() ? r1.getUserId() : r2.getUserId();
                }

                // Broadcast final result
                Map<String, Object> finalResult = Map.of(
                    "type", "BATTLE_COMPLETE",
                    "roomId", roomId,
                    "winnerId", winnerId,
                    "hostScore", r1.getScore(),
                    "hostTime", r1.getTime(),
                    "guestScore", r2.getScore(),
                    "guestTime", r2.getTime()
                );
                broadcastToRoom(roomId, finalResult);

                // Clear results for next game
                roomState.getResults().clear();
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
