package com.tokki.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokki.domain.PvpResult;
import com.tokki.repository.PvpResultRepository;
import com.tokki.service.PvpService;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class PvpWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final PvpService pvpService;
    private final PvpResultRepository pvpResultRepository;
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = extractRoomId(session.getUri());
        roomSessions.computeIfAbsent(roomId, key -> new CopyOnWriteArraySet<>()).add(session);
        session.getAttributes().put("roomId", roomId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (!(message instanceof TextMessage textMessage)) {
            return;
        }
        JsonNode json = objectMapper.readTree(textMessage.getPayload());
        String type = json.path("type").asText();
        String roomId = (String) session.getAttributes().get("roomId");

        if ("PROGRESS".equals(type)) {
            String uid = (String) session.getAttributes().get("uid");
            String payload = objectMapper.writeValueAsString(Map.of(
                "type", "PROGRESS",
                "userId", uid,
                "index", json.path("index").asInt(),
                "score", json.path("score").asInt()
            ));
            broadcastToRoom(roomId, payload, session);
            broadcastResultIfReady(roomId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        roomSessions.values().forEach(sessions -> sessions.remove(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcastResultIfReady(String roomId) throws IOException {
        if (pvpResultRepository.countByRoomRoomId(roomId) != 2) {
            return;
        }
        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, new CopyOnWriteArraySet<>());
        if (sessions.stream().anyMatch(session -> Boolean.TRUE.equals(session.getAttributes().get("resultSent")))) {
            return;
        }
        var results = pvpResultRepository.findByRoomRoomId(roomId);
        PvpResult winner = results.stream()
            .max(Comparator.comparingInt(PvpResult::getScore)
                .thenComparing(Comparator.comparing(PvpResult::getCompletionTime).reversed()))
            .orElse(null);
        if (winner == null) {
            return;
        }
        pvpService.updateWinner(roomId, winner.getUserId());
        Map<String, Object> payload = new ConcurrentHashMap<>();
        payload.put("type", "RESULT");
        payload.put("winnerId", winner.getUserId());
        results.forEach(result -> {
            String prefix = result.getUserId().equals(winner.getUserId()) ? "winner" : "loser";
            payload.put(prefix + "UserId", result.getUserId());
            payload.put(prefix + "Score", result.getScore());
            payload.put(prefix + "Time", result.getCompletionTime());
        });
        String json = objectMapper.writeValueAsString(payload);
        sessions.forEach(session -> session.getAttributes().put("resultSent", true));
        broadcastToRoom(roomId, json, null);
    }

    private void broadcastToRoom(String roomId, String payload, WebSocketSession exclude) throws IOException {
        for (WebSocketSession session : roomSessions.getOrDefault(roomId, new CopyOnWriteArraySet<>())) {
            if (!session.equals(exclude) && session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
        }
    }

    private String extractRoomId(URI uri) {
        if (uri == null) {
            return "";
        }
        String[] parts = uri.getPath().split("/");
        return parts.length == 0 ? "" : parts[parts.length - 1];
    }
}
