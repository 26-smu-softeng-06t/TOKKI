package com.tokki.repository;

import com.tokki.domain.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WordMissCountDto {
    private final Word word;
    private final Long totalCount;
}
