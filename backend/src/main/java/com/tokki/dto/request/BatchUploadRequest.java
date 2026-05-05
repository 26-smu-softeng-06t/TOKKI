package com.tokki.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BatchUploadRequest {
    @Valid
    @NotNull
    @Size(min = 1, message = "스테이지가 1개 이상이어야 합니다")
    private List<CreateStageRequest> stages = new ArrayList<>();
}
