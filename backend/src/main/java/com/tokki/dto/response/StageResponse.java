package com.tokki.dto.response;

import com.tokki.domain.Stage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StageResponse {
    private Long id;
    private String title;
    private String description;
    private Integer level;
    private LocalDateTime createdAt;

    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
                .id(stage.getId())
                .title(stage.getTitle())
                .description(stage.getDescription())
                .level(stage.getLevel())
                .createdAt(stage.getCreatedAt())
                .build();
    }
}
