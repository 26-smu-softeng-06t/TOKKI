package com.tokki.auth.service;

import com.tokki.config.properties.TokkiAuthProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthEventLogServiceTest {

    private static final TokkiAuthProperties AUTH_PROPERTIES =
            new TokkiAuthProperties(new TokkiAuthProperties.EventLog(100));

    @Test
    void recordsMostRecentAuthEventsFirst() {
        AuthEventLogService service = new AuthEventLogService(AUTH_PROPERTIES);

        service.recordSuccess("google", "user@example.com", "User");
        service.recordFailure("google", "oauth2_failed");

        assertThat(service.recent())
                .extracting(AuthEventLogService.AuthEventLogEntry::type)
                .containsExactly("OAUTH2_LOGIN_FAILURE", "OAUTH2_LOGIN_SUCCESS");
    }

    @Test
    void keepsOnlyLatestOneHundredEvents() {
        AuthEventLogService service = new AuthEventLogService(AUTH_PROPERTIES);

        for (int i = 0; i < 101; i += 1) {
            service.recordSuccess("google", "user%s@example.com".formatted(i), "User %s".formatted(i));
        }

        assertThat(service.recent()).hasSize(100);
        assertThat(service.recent().getLast().email()).isEqualTo("user1@example.com");
    }
}
