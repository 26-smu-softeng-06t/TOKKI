package com.tokki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WordRequest {
    private String wordId;

    @NotBlank
    private String word;

    @NotBlank
    private String meaning;

    private String example;

    @PositiveOrZero
    private int orderIndex;
}
