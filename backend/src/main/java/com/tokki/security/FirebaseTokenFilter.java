package com.tokki.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token != null && FirebaseApp.getApps().isEmpty()) {
            request.setAttribute(AuthFailureAttributes.ERROR_CODE, ErrorCode.TOKEN_INVALID);
        } else if (token != null) {
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                User user = userRepository.findById(decoded.getUid())
                        .orElseGet(() -> createUser(decoded));

                AuthUser authUser = new AuthUser(user.getUid(), user.getEmail(), user.getRole());
                String authority = "ROLE_" + user.getRole().name().toUpperCase();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        authUser, null, List.of(new SimpleGrantedAuthority(authority)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (FirebaseAuthException e) {
                request.setAttribute(AuthFailureAttributes.ERROR_CODE, tokenErrorCode(e));
                log.warn("Firebase token verification failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private User createUser(FirebaseToken decoded) {
        User newUser = User.builder()
                .uid(decoded.getUid())
                .nickname(decoded.getName() != null ? decoded.getName() : decoded.getUid())
                .email(decoded.getEmail())
                .role(UserRole.user)
                .build();
        return userRepository.save(newUser);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private ErrorCode tokenErrorCode(FirebaseAuthException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        return message.contains("expired") ? ErrorCode.TOKEN_EXPIRED : ErrorCode.TOKEN_INVALID;
    }
}
