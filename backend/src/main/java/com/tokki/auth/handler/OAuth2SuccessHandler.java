package com.tokki.auth.handler;

import com.tokki.auth.service.AuthEventLogService;
import com.tokki.config.properties.TokkiFrontendProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthEventLogService authEventLogService;
    private final TokkiFrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttribute("email");
        String name = (String) oAuth2User.getAttribute("name");
        String picture = (String) oAuth2User.getAttribute("picture");

        log.info("[OAuth2 Success] Email={}, Name={}", email, name);
        authEventLogService.recordSuccess("google", email, name);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendProperties.callbackUrl())
                .queryParam("email", email != null ? email : "")
                .queryParam("name", name != null ? name : "")
                .queryParam("picture", picture != null ? picture : "")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
