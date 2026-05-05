package com.tokki.controller;

import com.tokki.dto.request.SaveSessionRequest;
import com.tokki.dto.response.QuizSessionResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.security.SecurityUtils;
import com.tokki.service.QuizSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz-sessions")
@RequiredArgsConstructor
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    @GetMapping
    public ResponseEntity<QuizSessionResponse> getSession(@RequestParam String userId, @RequestParam String stageId) {
        requireSelf(userId);
        return ResponseEntity.ok(quizSessionService.getSession(userId, stageId));
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<Void> saveSession(@PathVariable String sessionId, @RequestBody @Valid SaveSessionRequest req) {
        requireSelf(req.getUserId());
        quizSessionService.saveSession(sessionId, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        quizSessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    private void requireSelf(String userId) {
        if (!SecurityUtils.getCurrentUid().equals(userId)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
