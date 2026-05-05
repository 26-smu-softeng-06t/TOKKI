package com.tokki.dto.response;

import com.tokki.domain.IncorrectWord;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IncorrectWordResponse {
    private Long wordId;
    private String korean;
    private String meaning;
    private Integer count;
    private LocalDateTime lastIncorrectAt;

    public static IncorrectWordResponse from(IncorrectWord iw) {
        return IncorrectWordResponse.builder()
                .wordId(iw.getWord().getId())
                .korean(iw.getWord().getKorean())
                .meaning(iw.getWord().getMeaning())
                .count(iw.getCount())
                .lastIncorrectAt(iw.getLastIncorrectAt())
                .build();
    }
}
