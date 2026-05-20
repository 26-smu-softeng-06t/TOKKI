package com.tokki.dto.response;

import com.tokki.domain.Stage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StageResponse {
    private Long stageId;
    private String difficulty;
    private Integer stageNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WordResponse> words;

    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
                .stageId(stage.getId())
                .difficulty(stage.getDifficulty().toApiValue())
                .stageNumber(stage.getStageNumber())
                .createdAt(stage.getCreatedAt())
                .updatedAt(stage.getUpdatedAt())
                .words(List.of())
                .build();
    }

    public static StageResponse from(Stage stage, List<WordResponse> words) {
        return StageResponse.builder()
                .stageId(stage.getId())
                .difficulty(stage.getDifficulty().toApiValue())
                .stageNumber(stage.getStageNumber())
                .createdAt(stage.getCreatedAt())
                .updatedAt(stage.getUpdatedAt())
                .words(words)
                .build();
    }
}
