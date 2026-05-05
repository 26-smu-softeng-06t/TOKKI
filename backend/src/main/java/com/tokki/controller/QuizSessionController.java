package com.tokki.controller;

import com.tokki.dto.request.SaveSessionRequest;
import com.tokki.dto.response.QuizSessionResponse;
import com.tokki.security.AuthUser;
import com.tokki.service.QuizSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    @PostMapping
    public ResponseEntity<QuizSessionResponse> saveSession(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveSessionRequest request) {
        return ResponseEntity.ok(quizSessionService.saveSession(authUser.getUid(), request));
    }

    @GetMapping
    public ResponseEntity<List<QuizSessionResponse>> getSessions(
            @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(quizSessionService.getUserSessions(authUser.getUid()));
    }
}
