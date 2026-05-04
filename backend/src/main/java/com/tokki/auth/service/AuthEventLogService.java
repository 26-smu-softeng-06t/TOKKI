package com.tokki.auth.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class AuthEventLogService {

    private static final int MAX_EVENTS = 100;

    private final ConcurrentLinkedDeque<AuthEventLogEntry> events = new ConcurrentLinkedDeque<>();

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
        while (events.size() > MAX_EVENTS) {
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
