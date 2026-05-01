package com.tokki.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/google-url")
    public ResponseEntity<Map<String, String>> getGoogleAuthorizationUrl() {
        String googleAuthUrl = "/oauth2/authorization/google";
        return ResponseEntity.ok(Map.of("authorizationUrl", googleAuthUrl));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> authStatus() {
        return ResponseEntity.ok(Map.of("status", "configured"));
    }
}