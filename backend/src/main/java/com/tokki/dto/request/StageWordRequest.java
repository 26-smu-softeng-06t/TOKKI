package com.tokki.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StageWordRequest {

    @NotBlank
    @Size(max = 100)
    private String word;

    @NotBlank
    @Size(max = 200)
    private String meaning;

    @Size(max = 500)
    private String example;

    @Size(max = 500)
    private String imageUrl;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer orderIndex;
}
