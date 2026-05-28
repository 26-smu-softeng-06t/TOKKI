package com.tokki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchStageRequest {

    @NotEmpty
    @Valid
    private List<CreateStageRequest> stages;
}
