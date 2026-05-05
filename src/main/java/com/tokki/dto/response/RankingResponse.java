package com.tokki.dto.response;

import com.tokki.domain.Ranking;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingResponse {
    private Integer rank;
    private String uid;
    private String nickname;
    private Integer score;
    private String period;

    public static RankingResponse from(Ranking ranking) {
        return RankingResponse.builder()
                .rank(ranking.getRank())
                .uid(ranking.getUser().getUid())
                .nickname(ranking.getUser().getNickname())
                .score(ranking.getScore())
                .period(ranking.getPeriod())
                .build();
    }
}
