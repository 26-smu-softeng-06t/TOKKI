package com.tokki.dto.response;

import com.tokki.domain.QuizSession;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuizSessionResponse {
    private Long id;
    private Long stageId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public static QuizSessionResponse from(QuizSession session) {
        return QuizSessionResponse.builder()
                .id(session.getId())
                .stageId(session.getStage().getId())
                .score(session.getScore())
                .totalQuestions(session.getTotalQuestions())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .build();
    }
}
