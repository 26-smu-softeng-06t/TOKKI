package com.tokki.dto.response;

import com.tokki.domain.Word;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WordResponse {
    private Long wordId;
    private Long stageId;
    private String word;
    private String meaning;
    private String example;
    private String imageUrl;
    private Integer orderIndex;

    public static WordResponse from(Word word) {
        return WordResponse.builder()
                .wordId(word.getId())
                .stageId(word.getStage().getId())
                .word(word.getWord())
                .meaning(word.getMeaning())
                .example(word.getExample())
                .imageUrl(word.getImageUrl())
                .orderIndex(word.getOrderIndex())
                .build();
    }
}
