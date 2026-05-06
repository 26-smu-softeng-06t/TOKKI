package com.tokki.auth.controller;

import com.tokki.auth.dto.AdminRegisterRequest;
import com.tokki.auth.dto.TokenResponse;
import com.tokki.auth.service.AuthEventLogService;
import com.tokki.auth.service.AuthService;
import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
import com.tokki.config.properties.TokkiAdminProperties;
import com.tokki.domain.User;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthEventLogService authEventLogService;
    private final TokkiAdminProperties adminProperties;

    @Value("${tokki.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @GetMapping("/google-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getGoogleAuthorizationUrl() {
        return ResponseEntity.ok(ApiResponses.data(Map.of("authorizationUrl", "/oauth2/authorization/google")));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, String>>> authStatus() {
        return ResponseEntity.ok(ApiResponses.data(Map.of("status", "configured")));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> currentUser(Authentication authentication) {
        if (!isOAuth2Authenticated(authentication)) {
            if (isBearerAuthenticated(authentication)) {
                AuthUser user = (AuthUser) authentication.getPrincipal();
                return ResponseEntity.ok(ApiResponses.data(Map.of(
                        "authenticated", true,
                        "provider", "bearer",
                        "providerId", user.getUid(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                )));
            }

            return ResponseEntity.ok(ApiResponses.data(Map.of("authenticated", (Object) false)));
        }

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String providerId = stringAttribute(user, "sub");
        Optional<User> savedUser = authService.findUser(providerId);

        return ResponseEntity.ok(ApiResponses.data(Map.of(
                "authenticated", true,
                "provider", "google",
                "providerId", providerId,
                "email", stringAttribute(user, "email"),
                "name", stringAttribute(user, "name"),
                "picture", stringAttribute(user, "picture"),
                "role", savedUser.map(value -> value.getRole().name()).orElse("user")
        )));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(ApiResponses.data(Map.of("status", "logged_out")));
    }

    @GetMapping("/events")
    public ResponseEntity<?> authEvents(
            @RequestHeader(value = "X-TOKKI-ADMIN-KEY", required = false) String adminKey
    ) {
        if (!hasValidAdminKey(adminKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponses.error(
                            "INVALID_ADMIN_KEY",
                            "A valid admin key is required to read auth events."
                    ));
        }

        return ResponseEntity.ok(ApiResponses.data(authEventLogService.recent()));
    }

    @PostMapping("/admin/register")
    public ApiResponse<TokenResponse> registerAdmin(
            Authentication authentication,
            @Valid @RequestBody AdminRegisterRequest request
    ) {
        String token = authService.registerAdmin(resolveUid(authentication), request.adminSecretKey());
        return new ApiResponse<>(TokenResponse.of(token, jwtExpirationMs / 1000));
    }

    @PostMapping("/token")
    public ApiResponse<TokenResponse> issueToken(
            Authentication authentication
    ) {
        String token = authService.issueToken(resolveUid(authentication));
        return new ApiResponse<>(TokenResponse.of(token, jwtExpirationMs / 1000));
    }

    private boolean hasValidAdminKey(String adminKey) {
        return StringUtils.hasText(adminProperties.secretKey()) && adminProperties.secretKey().equals(adminKey);
    }

    private boolean isOAuth2Authenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2User;
    }

    private boolean isBearerAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AuthUser;
    }

    private String resolveUid(Authentication authentication) {
        if (isBearerAuthenticated(authentication)) {
            return ((AuthUser) authentication.getPrincipal()).getUid();
        }
        if (isOAuth2Authenticated(authentication)) {
            return stringAttribute((OAuth2User) authentication.getPrincipal(), "sub");
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private static String stringAttribute(OAuth2User user, String name) {
        Object value = user.getAttribute(name);
        return value instanceof String stringValue ? stringValue : "";
    }
}
