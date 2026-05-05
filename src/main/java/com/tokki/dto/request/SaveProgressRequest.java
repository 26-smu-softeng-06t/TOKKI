package com.tokki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveProgressRequest {

    @NotNull
    private Long stageId;

    @NotNull
    private Boolean completed;
}
