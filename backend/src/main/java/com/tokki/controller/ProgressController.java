package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
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
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getProgress(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(ApiResponses.data(progressService.getUserProgress(authUser.getUid())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProgressResponse>> saveProgress(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveProgressRequest request) {
        return ResponseEntity.ok(ApiResponses.data(progressService.saveProgress(authUser.getUid(), request)));
    }

    @GetMapping("/incorrect-words")
    public ResponseEntity<ApiResponse<List<IncorrectWordResponse>>> getIncorrectWords(
            @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(ApiResponses.data(progressService.getIncorrectWords(authUser.getUid())));
    }
    @DeleteMapping("/incorrect/{wordId}")
    public ResponseEntity<Void> deleteIncorrectWord(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long wordId) {
        progressService.deleteIncorrectWord(authUser.getUid(), wordId);
        return ResponseEntity.noContent().build();
    }
}
