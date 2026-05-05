package com.tokki.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaveSessionRequest {

    @NotNull
    private Long stageId;

    @NotNull
    @Min(0)
    private Integer score;

    @NotNull
    @Min(1)
    private Integer totalQuestions;

    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long wordId;
        private String userAnswer;
        private Boolean correct;
    }
}
