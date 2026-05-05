package com.tokki.dto.response;

import com.tokki.domain.PvpRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PvpRoomResponse {
    private Long id;
    private String hostUid;
    private String guestUid;
    private String status;
    private Long stageId;
    private LocalDateTime createdAt;

    public static PvpRoomResponse from(PvpRoom room) {
        return PvpRoomResponse.builder()
                .id(room.getId())
                .hostUid(room.getHostUser().getUid())
                .guestUid(room.getGuestUser() != null ? room.getGuestUser().getUid() : null)
                .status(room.getStatus())
                .stageId(room.getStage().getId())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
