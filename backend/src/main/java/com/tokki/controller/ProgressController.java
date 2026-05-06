package com.tokki.controller;

import com.tokki.dto.request.SaveProgressRequest;
import com.tokki.dto.response.IncorrectWordResponse;
import com.tokki.dto.response.ProgressResponse;
import com.tokki.security.AuthUser;
import com.tokki.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<List<ProgressResponse>> getProgress(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(progressService.getUserProgress(authUser.getUid()));
    }

    @PostMapping
    public ResponseEntity<ProgressResponse> saveProgress(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveProgressRequest request) {
        return ResponseEntity.ok(progressService.saveProgress(authUser.getUid(), request));
    }

    @GetMapping("/incorrect-words")
    public ResponseEntity<List<IncorrectWordResponse>> getIncorrectWords(
            @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(progressService.getIncorrectWords(authUser.getUid()));
    }
    @DeleteMapping("/incorrect/{wordId}")
    public ResponseEntity<Void> deleteIncorrectWord(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long wordId) {
        progressService.deleteIncorrectWord(authUser.getUid(), wordId);
        return ResponseEntity.noContent().build();
    }
}
