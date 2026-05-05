package com.tokki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinRoomRequest {

    @NotNull
    private Long roomId;
}
