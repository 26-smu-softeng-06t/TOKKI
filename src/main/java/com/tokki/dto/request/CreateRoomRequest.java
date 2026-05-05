package com.tokki.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoomRequest {

    @NotNull
    private Long stageId;
}
