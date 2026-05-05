package com.tokki.dto.response;

import com.tokki.domain.UserProgress;
import java.util.List;
import lombok.Builder;

@Builder
public record ProgressResponse(
    String progressId,
    String userId,
    String stageId,
    String updatedAt,
    boolean completed,
    int lastScore,
    List<IncorrectWordResponse> incorrectWords
) {
    public static ProgressResponse from(UserProgress up) {
        return ProgressResponse.builder()
            .progressId(up.getProgressId())
            .userId(up.getUserId())
            .stageId(up.getStageId())
            .updatedAt(up.getUpdatedAt().toString())
            .completed(up.isCompleted())
            .lastScore(up.getLastScore())
            .incorrectWords(up.getIncorrectWords().stream().map(IncorrectWordResponse::from).toList())
            .build();
    }
}
