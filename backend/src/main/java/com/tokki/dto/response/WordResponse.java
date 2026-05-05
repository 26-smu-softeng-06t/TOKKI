package com.tokki.dto.response;

import com.tokki.domain.Word;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WordResponse {
    private Long id;
    private Long stageId;
    private String korean;
    private String meaning;
    private String example;
    private String imageUrl;

    public static WordResponse from(Word word) {
        return WordResponse.builder()
                .id(word.getId())
                .stageId(word.getStage().getId())
                .korean(word.getKorean())
                .meaning(word.getMeaning())
                .example(word.getExample())
                .imageUrl(word.getImageUrl())
                .build();
    }
}
