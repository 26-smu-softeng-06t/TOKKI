package com.tokki.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminRegisterRequest(
        @NotBlank(message = "관리자 비밀키를 입력해주세요.")
        String adminSecretKey
) {
}