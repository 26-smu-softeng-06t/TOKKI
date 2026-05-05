package com.tokki.dto.request;

import com.tokki.domain.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateStageRequest {
    private String stageId;

    @NotNull
    private DifficultyLevel difficulty;

    @Positive
    private int stageNumber;

    @Valid
    @Size(min = 1, message = "단어가 1개 이상이어야 합니다")
    private List<WordRequest> words = new ArrayList<>();
}
