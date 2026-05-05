package com.tokki.config;

import com.tokki.websocket.FirebaseHandshakeInterceptor;
import com.tokki.websocket.PvpWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final PvpWebSocketHandler pvpWebSocketHandler;
    private final FirebaseHandshakeInterceptor firebaseHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pvpWebSocketHandler, "/ws/pvp/{roomId}")
            .addInterceptors(firebaseHandshakeInterceptor)
            .setAllowedOrigins("http://localhost:3000");
    }
}
