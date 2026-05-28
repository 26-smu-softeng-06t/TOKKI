package com.tokki.config;

import com.tokki.auth.handler.OAuth2FailureHandler;
import com.tokki.auth.handler.OAuth2SuccessHandler;
import com.tokki.auth.service.CustomOAuth2UserService;
import com.tokki.repository.UserRepository;
import com.tokki.security.DevAuthenticationFilter;
import com.tokki.security.FirebaseTokenFilter;
import com.tokki.security.JwtAuthenticationFilter;
import com.tokki.security.JwtProvider;
import com.tokki.security.RestAccessDeniedHandler;
import com.tokki.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider) {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Bean
    public FirebaseTokenFilter firebaseTokenFilter(UserRepository userRepository) {
        return new FirebaseTokenFilter(userRepository);
    }

    @Bean
    public DevAuthenticationFilter devAuthenticationFilter(UserRepository userRepository) {
        return new DevAuthenticationFilter(userRepository);
    }

    /**
     * OAuth2 핸드셰이크 전용 체인. state 파라미터를 세션에 저장해야 하므로 IF_REQUIRED 유지.
     * /oauth2/** 와 /login/oauth2/** 경로만 이 체인이 처리한다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                )
                .build();
    }

    /**
     * API 전용 체인. JWT/Firebase Bearer 토큰으로 인증하므로 STATELESS.
     * OAuth2 핸드셰이크 경로는 Order(1) 체인이 먼저 처리한다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              DevAuthenticationFilter devAuthenticationFilter,
                                              JwtAuthenticationFilter jwtAuthenticationFilter,
                                              FirebaseTokenFilter firebaseTokenFilter,
                                              RestAuthenticationEntryPoint authenticationEntryPoint,
                                              RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/google-url",
                                "/api/auth/status",
                                "/api/auth/me",
                                "/api/auth/logout",
                                "/api/auth/events",
                                "/auth-test/**",
                                "/ws/**",
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stages/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // FirebaseTokenFilter first (handles Firebase ID tokens with RS256)
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // JwtAuthenticationFilter second (handles custom JWT tokens with HMAC)
                .addFilterAfter(jwtAuthenticationFilter, FirebaseTokenFilter.class)
                // DevAuthenticationFilter last (for local development)
                .addFilterAfter(devAuthenticationFilter, JwtAuthenticationFilter.class)
                .build();
    }
}
