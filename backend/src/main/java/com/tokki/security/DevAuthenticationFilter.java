package com.tokki.security;

import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class DevAuthenticationFilter extends OncePerRequestFilter {

    private static final String DEV_USER_HEADER = "X-TOKKI-DEV-USER";
    private static final String DEV_ROLE_HEADER = "X-TOKKI-DEV-ROLE";
    private static final String DEV_UID = "dev-user-localhost";
    private static final String DEV_EMAIL = "dev@localhost";

    private final UserRepository userRepository;

    public DevAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null || !isDevAuthRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserRole role = resolveRole(request.getHeader(DEV_ROLE_HEADER));

        // Ensure dev user exists in database
        User devUser = userRepository.findById(DEV_UID)
                .orElseGet(() -> userRepository.save(User.builder()
                        .uid(DEV_UID)
                        .nickname("Dev User")
                        .email(DEV_EMAIL)
                        .role(UserRole.user)
                        .build()));

        // Update role if needed
        if (!devUser.getRole().equals(role)) {
            devUser.updateRole(role);
            devUser = userRepository.save(devUser);
        }

        AuthUser authUser = new AuthUser(DEV_UID, DEV_EMAIL, role);
        String authority = "ROLE_" + role.name().toUpperCase();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                authUser, null, List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private boolean isDevAuthRequest(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader(DEV_USER_HEADER)) && isLoopbackRequest(request);
    }

    private boolean isLoopbackRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (!StringUtils.hasText(remoteAddr)) {
            return false;
        }

        try {
            return InetAddress.getByName(remoteAddr).isLoopbackAddress();
        } catch (Exception ignored) {
            return "127.0.0.1".equals(remoteAddr)
                    || "0:0:0:0:0:0:0:1".equals(remoteAddr)
                    || "::1".equals(remoteAddr);
        }
    }

    private UserRole resolveRole(String rawRole) {
        if ("admin".equalsIgnoreCase(rawRole)) {
            return UserRole.admin;
        }
        return UserRole.user;
    }
}
