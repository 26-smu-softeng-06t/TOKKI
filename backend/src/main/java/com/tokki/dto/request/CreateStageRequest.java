package com.tokki.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateStageRequest {

    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @Min(1)
    private Integer level;

    @NotBlank
    @Size(max = 20)
    private String difficulty;

    @NotNull
    @Min(1)
    private Integer stageNumber;

    @Valid
    @NotEmpty
    private List<WordItem> words = new ArrayList<>();

    public String resolvedTitle() {
        return title != null && !title.isBlank() ? title : difficulty;
    }

    public Integer resolvedLevel() {
        return level != null ? level : stageNumber;
    }

    @Data
    public static class WordItem {
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
        private Integer orderIndex;
    }
}
