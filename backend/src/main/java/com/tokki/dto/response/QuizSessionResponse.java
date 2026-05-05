package com.tokki.dto.response;

import com.tokki.domain.QuizMode;
import com.tokki.domain.QuizSession;
import java.util.List;
import lombok.Builder;

@Builder
public record QuizSessionResponse(
    String sessionId,
    String userId,
    String stageId,
    String savedAt,
    QuizMode mode,
    int currentIndex,
    List<QuizAnswerResponse> answers
) {
    public static QuizSessionResponse from(QuizSession session) {
        return QuizSessionResponse.builder()
            .sessionId(session.getSessionId())
            .userId(session.getUserId())
            .stageId(session.getStageId())
            .savedAt(session.getSavedAt().toString())
            .mode(session.getMode())
            .currentIndex(session.getCurrentIndex())
            .answers(session.getAnswers().stream().map(QuizAnswerResponse::from).toList())
            .build();
    }
}
