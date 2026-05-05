package com.tokki.dto.response;

import com.tokki.domain.PvpRoom;
import com.tokki.domain.PvpStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record PvpRoomResponse(
    String roomId,
    String inviteCode,
    String hostUserId,
    String guestUserId,
    String stageId,
    PvpStatus status,
    String createdAt,
    List<PvpResultResponse> results
) {
    public static PvpRoomResponse from(PvpRoom room) {
        return PvpRoomResponse.builder()
            .roomId(room.getRoomId())
            .inviteCode(room.getInviteCode())
            .hostUserId(room.getHostUserId())
            .guestUserId(room.getGuestUserId())
            .stageId(room.getStageId())
            .status(room.getStatus())
            .createdAt(room.getCreatedAt().toString())
            .results(room.getResults().stream().map(PvpResultResponse::from).toList())
            .build();
    }
}
