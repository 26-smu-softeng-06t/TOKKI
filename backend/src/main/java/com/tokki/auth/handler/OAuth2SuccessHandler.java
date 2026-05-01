package com.tokki.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String DEFAULT_FRONTEND_CALLBACK_URL = "http://localhost:5173/login/callback";

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

        String redirectUrl = DEFAULT_FRONTEND_CALLBACK_URL
                + "?email=" + URLEncoder.encode(email != null ? email : "", "UTF-8")
                + "&name=" + URLEncoder.encode(name != null ? name : "", "UTF-8")
                + "&picture=" + URLEncoder.encode(picture != null ? picture : "", "UTF-8");

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private static String URLEncoder(String value, String encoding) {
        try {
            return java.net.URLEncoder.encode(value, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }
}