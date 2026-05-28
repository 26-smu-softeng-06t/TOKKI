package com.tokki.dto.response;

import com.tokki.domain.PvpResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PvpResultResponse {
    private Long roomId;
    private String uid;
    private Integer score;
    private String result;
    private LocalDateTime createdAt;

    public static PvpResultResponse from(PvpResult pvpResult) {
        return PvpResultResponse.builder()
                .roomId(pvpResult.getRoom().getId())
                .uid(pvpResult.getUser().getUid())
                .score(pvpResult.getScore())
                .result(pvpResult.getResult())
                .createdAt(pvpResult.getCreatedAt())
                .build();
    }
}
