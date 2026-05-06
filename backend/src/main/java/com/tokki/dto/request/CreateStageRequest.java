package com.tokki.dto.request;

import com.tokki.domain.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateStageRequest {

    @NotNull
    private DifficultyLevel difficulty;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer stageNumber;

    @Valid
    private List<StageWordRequest> words = List.of();
}
