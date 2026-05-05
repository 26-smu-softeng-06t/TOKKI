package com.tokki.dto.response;

import com.tokki.domain.Ranking;
import lombok.Builder;

@Builder
public record RankingResponse(
    String rankingId,
    String wordId,
    String word,
    String meaning,
    int missCount,
    int rank,
    String updatedAt
) {
    public static RankingResponse from(Ranking ranking) {
        return RankingResponse.builder()
            .rankingId(ranking.getRankingId())
            .wordId(ranking.getWordId())
            .word(ranking.getWord())
            .meaning(ranking.getMeaning())
            .missCount(ranking.getMissCount())
            .rank(ranking.getRank())
            .updatedAt(ranking.getUpdatedAt().toString())
            .build();
    }
}
