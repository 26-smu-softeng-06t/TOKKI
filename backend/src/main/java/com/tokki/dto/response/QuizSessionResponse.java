package com.tokki.dto.response;

import com.tokki.domain.QuizSession;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class QuizSessionResponse {
    private Long id;
    private Long stageId;
    private String mode;
    private Integer currentIndex;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<AnswerDetail> answers;

    @Getter
    @Builder
    public static class AnswerDetail {
        private Long wordId;
        private String word;
        private String meaning;
        private String userAnswer;
        private Boolean correct;
    }

    public static QuizSessionResponse from(QuizSession session) {
        return QuizSessionResponse.builder()
                .id(session.getId())
                .stageId(session.getStage().getId())
                .mode(session.getMode() != null ? session.getMode().name() : null)
                .currentIndex(session.getCurrentIndex())
                .score(session.getScore())
                .totalQuestions(session.getTotalQuestions())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .answers(null)
                .build();
    }

    public static QuizSessionResponse fromWithDetails(QuizSession session) {
        List<AnswerDetail> answers = session.getAnswers() != null
            ? session.getAnswers().stream()
                .map(a -> AnswerDetail.builder()
                    .wordId(a.getWord().getId())
                    .word(a.getWord().getWord())
                    .meaning(a.getWord().getMeaning())
                    .userAnswer(a.getUserAnswer())
                    .correct(a.getCorrect())
                    .build())
                .toList()
            : List.of();

        return QuizSessionResponse.builder()
                .id(session.getId())
                .stageId(session.getStage().getId())
                .mode(session.getMode() != null ? session.getMode().name() : null)
                .currentIndex(session.getCurrentIndex())
                .score(session.getScore())
                .totalQuestions(session.getTotalQuestions())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .answers(answers)
                .build();
    }
}
