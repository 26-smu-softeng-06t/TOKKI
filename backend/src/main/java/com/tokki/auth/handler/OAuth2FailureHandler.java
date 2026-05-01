package com.tokki.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String DEFAULT_FRONTEND_LOGIN_URL = "http://localhost:5173/login";

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.error("[OAuth2 Failure] Error={}", exception.getMessage(), exception);

        String redirectUrl = DEFAULT_FRONTEND_LOGIN_URL
                + "?error=" + java.net.URLEncoder.encode(
                        exception.getMessage() != null ? exception.getMessage() : "oauth2_failed",
                        "UTF-8"
                );

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}