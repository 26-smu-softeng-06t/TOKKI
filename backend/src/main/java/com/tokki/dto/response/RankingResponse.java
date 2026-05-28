package com.tokki.dto.response;

import com.tokki.domain.Ranking;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingResponse {
    private Long id;
    private Long wordId;
    private String word;
    private String meaning;
    private Integer missCount;
    private Integer rank;
    private String period;
    private String snapshotDate;

    public static RankingResponse from(Ranking ranking) {
        return RankingResponse.builder()
                .id(ranking.getId())
                .wordId(ranking.getWord().getId())
                .word(ranking.getWord().getWord())
                .meaning(ranking.getWord().getMeaning())
                .missCount(ranking.getMissCount())
                .rank(ranking.getRank())
                .period(ranking.getPeriod())
                .snapshotDate(ranking.getSnapshotDate().toString())
                .build();
    }
}
