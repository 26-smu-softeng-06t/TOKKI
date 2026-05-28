package com.tokki.dto.response;

import com.tokki.domain.IncorrectWord;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IncorrectWordResponse {
    private Long wordId;
    private String word;
    private String meaning;
    private Integer count;
    private LocalDateTime lastIncorrectAt;

    public static IncorrectWordResponse from(IncorrectWord iw) {
        return IncorrectWordResponse.builder()
                .wordId(iw.getWord().getId())
                .word(iw.getWord().getWord())
                .meaning(iw.getWord().getMeaning())
                .count(iw.getCount())
                .lastIncorrectAt(iw.getLastIncorrectAt())
                .build();
    }
}
