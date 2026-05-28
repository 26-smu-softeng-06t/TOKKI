package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
import com.tokki.dto.request.SaveSessionRequest;
import com.tokki.dto.request.UpsertDraftSessionRequest;
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
    public ResponseEntity<ApiResponse<QuizSessionResponse>> saveSession(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveSessionRequest request) {
        return ResponseEntity.ok(ApiResponses.data(quizSessionService.saveSession(authUser.getUid(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizSessionResponse>>> getSessions(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) Long stageId,
            @RequestParam(required = false) Boolean completed) {
        if (completed != null && completed) {
            return ResponseEntity.ok(ApiResponses.data(quizSessionService.getCompletedSessions(authUser.getUid(), stageId)));
        }
        if (completed != null && !completed) {
            return ResponseEntity.ok(ApiResponses.data(quizSessionService.getDraftSessions(authUser.getUid())));
        }
        return ResponseEntity.ok(ApiResponses.data(quizSessionService.getUserSessions(authUser.getUid())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizSessionResponse>> getSession(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponses.data(quizSessionService.getSessionById(id, authUser.getUid())));
    }

    @PutMapping("/current")
    public ResponseEntity<ApiResponse<QuizSessionResponse>> upsertDraftSession(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpsertDraftSessionRequest request) {
        return ResponseEntity.ok(ApiResponses.data(quizSessionService.upsertDraftSession(authUser.getUid(), request)));
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> deleteDraftSession(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long stageId) {
        quizSessionService.deleteDraftSession(authUser.getUid(), stageId);
        return ResponseEntity.noContent().build();
    }
}
