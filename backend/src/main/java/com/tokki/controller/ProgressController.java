package com.tokki.controller;

import com.tokki.dto.request.SaveProgressRequest;
import com.tokki.dto.response.IncorrectWordResponse;
import com.tokki.dto.response.ProgressResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.security.SecurityUtils;
import com.tokki.service.ProgressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<ProgressResponse> getProgress(@RequestParam String userId, @RequestParam String stageId) {
        requireSelf(userId);
        return ResponseEntity.ok(progressService.getProgress(userId, stageId));
    }

    @PostMapping
    public ResponseEntity<Void> saveProgress(@RequestBody @Valid SaveProgressRequest req) {
        requireSelf(req.getUserId());
        progressService.saveProgress(req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/incorrect")
    public ResponseEntity<List<IncorrectWordResponse>> getIncorrectWords(@RequestParam String userId) {
        requireSelf(userId);
        return ResponseEntity.ok(progressService.getIncorrectWords(userId));
    }

    @PatchMapping("/{progressId}/incorrect/{wordId}")
    public ResponseEntity<Void> resolveIncorrectWord(@PathVariable String progressId, @PathVariable String wordId) {
        progressService.resolveIncorrectWord(progressId, wordId);
        return ResponseEntity.noContent().build();
    }

    private void requireSelf(String userId) {
        if (!SecurityUtils.getCurrentUid().equals(userId)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
