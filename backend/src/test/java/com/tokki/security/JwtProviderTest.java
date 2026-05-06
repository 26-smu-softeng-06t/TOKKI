package com.tokki.security;

import com.tokki.domain.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static final String SECRET = "tokki-test-secret-that-is-long-enough-for-hmac";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void generatesTokenWithUserClaims() {
        JwtProvider provider = new JwtProvider(SECRET, 60_000);

        String token = provider.generateToken("uid-1", "admin@example.com", UserRole.admin);
        Claims claims = provider.validateAndGetClaims(token);

        assertThat(claims.getSubject()).isEqualTo("uid-1");
        assertThat(claims.get("email", String.class)).isEqualTo("admin@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("admin");
    }

    @Test
    void setsAuthenticationWithRoleAuthority() {
        JwtProvider provider = new JwtProvider(SECRET, 60_000);
        String token = provider.generateToken("uid-1", "admin@example.com", UserRole.admin);

        provider.setAuthentication(token);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
