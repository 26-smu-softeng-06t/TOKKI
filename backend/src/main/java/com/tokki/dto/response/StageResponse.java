package com.tokki.dto.response;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record StageResponse(
    String stageId,
    DifficultyLevel difficulty,
    int stageNumber,
    String createdAt,
    String updatedAt,
    List<WordResponse> words
) {
    public static StageResponse from(Stage stage) {
        return StageResponse.builder()
            .stageId(stage.getStageId())
            .difficulty(stage.getDifficulty())
            .stageNumber(stage.getStageNumber())
            .createdAt(stage.getCreatedAt().toString())
            .updatedAt(stage.getUpdatedAt().toString())
            .words(stage.getWords().stream()
                .sorted(Comparator.comparingInt(Word -> Word.getOrderIndex()))
                .map(WordResponse::from)
                .toList())
            .build();
    }
}
