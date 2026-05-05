package com.tokki.auth.handler;

import com.tokki.auth.service.AuthEventLogService;
import com.tokki.config.properties.TokkiFrontendProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthEventLogService authEventLogService;
    private final TokkiFrontendProperties frontendProperties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.error("[OAuth2 Failure] Error={}", exception.getMessage(), exception);
        authEventLogService.recordFailure(
                "google",
                exception.getMessage() != null ? exception.getMessage() : "oauth2_failed"
        );

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendProperties.loginUrl())
                .queryParam("error", exception.getMessage() != null ? exception.getMessage() : "oauth2_failed")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
