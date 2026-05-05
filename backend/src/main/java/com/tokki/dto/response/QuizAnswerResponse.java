package com.tokki.dto.response;

import com.tokki.domain.QuizAnswer;
import lombok.Builder;

@Builder
public record QuizAnswerResponse(String answerId, String sessionId, String wordId, String userAnswer, boolean isCorrect) {
    public static QuizAnswerResponse from(QuizAnswer answer) {
        return QuizAnswerResponse.builder()
            .answerId(answer.getAnswerId())
            .sessionId(answer.getSession().getSessionId())
            .wordId(answer.getWordId())
            .userAnswer(answer.getUserAnswer())
            .isCorrect(answer.isCorrect())
            .build();
    }
}
