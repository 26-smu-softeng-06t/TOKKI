package com.tokki.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpsertUserRequest {
    @NotBlank
    private String uid;

    @NotBlank
    @Email
    private String email;
}
