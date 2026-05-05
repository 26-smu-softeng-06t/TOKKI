package com.tokki.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveResultRequest {

    @NotNull
    private Long roomId;

    @NotNull
    @Min(0)
    private Integer score;
}
