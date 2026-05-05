package com.tokki.dto.response;

import com.tokki.domain.Word;
import lombok.Builder;

@Builder
public record WordResponse(String wordId, String stageId, String word, String meaning, String example, int orderIndex) {
    public static WordResponse from(Word word) {
        return WordResponse.builder()
            .wordId(word.getWordId())
            .stageId(word.getStage().getStageId())
            .word(word.getWord())
            .meaning(word.getMeaning())
            .example(word.getExample())
            .orderIndex(word.getOrderIndex())
            .build();
    }
}
