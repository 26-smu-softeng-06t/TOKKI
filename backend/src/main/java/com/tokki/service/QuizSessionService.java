package com.tokki.service;

import com.tokki.domain.QuizAnswer;
import com.tokki.domain.QuizSession;
import com.tokki.dto.request.SaveSessionRequest;
import com.tokki.dto.response.QuizSessionResponse;
import com.tokki.repository.QuizSessionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;

    @Transactional(readOnly = true)
    public QuizSessionResponse getSession(String userId, String stageId) {
        return quizSessionRepository.findByUserIdAndStageId(userId, stageId)
            .map(QuizSessionResponse::from)
            .orElse(null);
    }

    public void saveSession(String sessionId, SaveSessionRequest req) {
        QuizSession session = quizSessionRepository.findById(sessionId)
            .orElseGet(() -> QuizSession.builder()
                .sessionId(sessionId)
                .userId(req.getUserId())
                .stageId(req.getStageId())
                .build());
        session.setMode(req.getMode());
        session.setCurrentIndex(req.getCurrentIndex());
        session.setSavedAt(LocalDateTime.now());
        session.getAnswers().clear();
        req.getAnswers().forEach(ar -> session.getAnswers().add(QuizAnswer.builder()
            .answerId(ar.getAnswerId())
            .session(session)
            .wordId(ar.getWordId())
            .userAnswer(ar.getUserAnswer())
            .isCorrect(ar.isCorrect())
            .build()));
        quizSessionRepository.save(session);
    }

    public void deleteSession(String sessionId) {
        quizSessionRepository.deleteById(sessionId);
    }
}
