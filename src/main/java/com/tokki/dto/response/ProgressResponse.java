package com.tokki.dto.response;

import com.tokki.domain.UserProgress;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProgressResponse {
    private Long stageId;
    private Boolean completed;
    private LocalDateTime completedAt;

    public static ProgressResponse from(UserProgress progress) {
        return ProgressResponse.builder()
                .stageId(progress.getStage().getId())
                .completed(progress.getCompleted())
                .completedAt(progress.getCompletedAt())
                .build();
    }
}
