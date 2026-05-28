package com.tokki.auth.service;

import com.tokki.config.properties.TokkiAuthProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class AuthEventLogService {

    private final ConcurrentLinkedDeque<AuthEventLogEntry> events = new ConcurrentLinkedDeque<>();
    private final int maxEvents;

    public AuthEventLogService(TokkiAuthProperties properties) {
        this.maxEvents = properties.eventLog().maxEvents();
    }

    public void recordSuccess(String provider, String email, String name) {
        add(new AuthEventLogEntry(
                Instant.now(),
                "OAUTH2_LOGIN_SUCCESS",
                provider,
                email,
                name,
                "Google OAuth2 login completed"
        ));
    }

    public void recordFailure(String provider, String message) {
        add(new AuthEventLogEntry(
                Instant.now(),
                "OAUTH2_LOGIN_FAILURE",
                provider,
                null,
                null,
                message
        ));
    }

    public List<AuthEventLogEntry> recent() {
        return new ArrayList<>(events);
    }

    private void add(AuthEventLogEntry event) {
        events.addFirst(event);
        while (events.size() > maxEvents) {
            events.removeLast();
        }
    }

    public record AuthEventLogEntry(
            Instant occurredAt,
            String type,
            String provider,
            String email,
            String name,
            String message
    ) {
    }
}
