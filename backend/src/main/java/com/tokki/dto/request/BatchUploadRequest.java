package com.tokki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchUploadRequest {

    @Valid
    @NotEmpty
    private List<CreateStageRequest> stages = new ArrayList<>();
}
