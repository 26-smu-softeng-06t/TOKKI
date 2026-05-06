package com.tokki.dto.response;

import com.tokki.domain.Stage;
import com.tokki.dto.response.WordResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StageResponse {
    private Long stageId;
    private Long id;
    private String difficulty;
    private Integer stageNumber;
    private String title;
    private String description;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WordResponse> words;

    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
                .stageId(stage.getId())
                .id(stage.getId())
                .difficulty(stage.getDifficulty().name())
                .stageNumber(stage.getStageNumber())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .level(stage.getLevel())
                .createdAt(stage.getCreatedAt())
                .updatedAt(stage.getCreatedAt())
                .words(List.of())
                .build();
    }

    public static StageResponse from(Stage stage, List<WordResponse> words) {
        return StageResponse.builder()
                .stageId(stage.getId())
                .id(stage.getId())
                .difficulty(stage.getDifficulty().name())
                .stageNumber(stage.getStageNumber())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .level(stage.getLevel())
                .createdAt(stage.getCreatedAt())
                .updatedAt(stage.getCreatedAt())
                .words(words)
                .build();
    }
}
