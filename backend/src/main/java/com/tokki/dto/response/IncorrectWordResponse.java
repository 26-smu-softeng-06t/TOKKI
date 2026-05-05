package com.tokki.dto.response;

import com.tokki.domain.IncorrectWord;
import lombok.Builder;

@Builder
public record IncorrectWordResponse(String incorrectWordId, String progressId, String wordId, boolean isResolved) {
    public static IncorrectWordResponse from(IncorrectWord iw) {
        return IncorrectWordResponse.builder()
            .incorrectWordId(iw.getIncorrectWordId())
            .progressId(iw.getProgress().getProgressId())
            .wordId(iw.getWordId())
            .isResolved(iw.isResolved())
            .build();
    }
}
