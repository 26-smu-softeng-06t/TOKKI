package com.tokki.dto.response;

import com.tokki.domain.Stage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StageResponse {
    private Long id;
    private Long stageId;
    private String title;
    private String description;
    private Integer level;
    private String difficulty;
    private Integer stageNumber;
    private LocalDateTime createdAt;

    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
                .id(stage.getId())
                .stageId(stage.getId())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .level(stage.getLevel())
                .difficulty(difficulty(stage.getTitle()))
                .stageNumber(stage.getLevel())
                .createdAt(stage.getCreatedAt())
                .build();
    }

    private static String difficulty(String title) {
        if ("easy".equals(title) || "medium".equals(title) || "hard".equals(title)) {
            return title;
        }
        return null;
    }
}
