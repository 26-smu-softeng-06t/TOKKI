package com.tokki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveProgressRequest {
    private String progressId;

    @NotBlank
    private String userId;

    @NotBlank
    private String stageId;

    private boolean completed;
    private int lastScore;

    @Valid
    private List<IncorrectWordRequest> incorrectWords = new ArrayList<>();
}
