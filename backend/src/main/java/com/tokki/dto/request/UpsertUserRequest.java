package com.tokki.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpsertUserRequest {

    @NotBlank
    @Size(max = 50)
    private String nickname;

    @Size(max = 100)
    private String email;
}
