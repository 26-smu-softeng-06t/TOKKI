package com.tokki.dto.response;

import com.tokki.domain.PvpResult;
import lombok.Builder;

@Builder
public record PvpResultResponse(
    String resultId,
    String roomId,
    String userId,
    int score,
    float completionTime,
    boolean isWinner
) {
    public static PvpResultResponse from(PvpResult result) {
        return PvpResultResponse.builder()
            .resultId(result.getResultId())
            .roomId(result.getRoom().getRoomId())
            .userId(result.getUserId())
            .score(result.getScore())
            .completionTime(result.getCompletionTime())
            .isWinner(result.isWinner())
            .build();
    }
}
