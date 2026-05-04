package com.tokki.auth.controller;

import com.tokki.auth.service.AuthEventLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthEventLogService authEventLogService;

    @Value("${tokki.admin.secret-key:}")
    private String adminSecretKey;

    @GetMapping("/google-url")
    public ResponseEntity<Map<String, Map<String, String>>> getGoogleAuthorizationUrl() {
        return ResponseEntity.ok(data(Map.of("authorizationUrl", "/oauth2/authorization/google")));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Map<String, String>>> authStatus() {
        return ResponseEntity.ok(data(Map.of("status", "configured")));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, ?>> currentUser(Authentication authentication) {
        if (!isOAuth2Authenticated(authentication)) {
            return ResponseEntity.ok(Map.of("data", Map.of("authenticated", false)));
        }

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of("data", Map.of(
                "authenticated", true,
                "provider", "google",
                "providerId", stringAttribute(user, "sub"),
                "email", stringAttribute(user, "email"),
                "name", stringAttribute(user, "name"),
                "picture", stringAttribute(user, "picture")
        )));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Map<String, String>>> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(data(Map.of("status", "logged_out")));
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, ?>> authEvents(
            @RequestHeader(value = "X-TOKKI-ADMIN-KEY", required = false) String adminKey
    ) {
        if (!hasValidAdminKey(adminKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", Map.of(
                            "code", "INVALID_ADMIN_KEY",
                            "message", "A valid admin key is required to read auth events."
                    )));
        }

        return ResponseEntity.ok(Map.of("data", authEventLogService.recent()));
    }

    private boolean hasValidAdminKey(String adminKey) {
        return StringUtils.hasText(adminSecretKey) && adminSecretKey.equals(adminKey);
    }

    private boolean isOAuth2Authenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2User;
    }

    private static String stringAttribute(OAuth2User user, String name) {
        Object value = user.getAttribute(name);
        return value instanceof String stringValue ? stringValue : "";
    }

    private static Map<String, Map<String, String>> data(Map<String, String> value) {
        return Map.of("data", value);
    }
}
