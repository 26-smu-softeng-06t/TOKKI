package com.tokki.dto.response;

import com.tokki.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(String uid, String email, String role, String createdAt) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
            .uid(user.getUid())
            .email(user.getEmail())
            .role(user.getRole().name())
            .createdAt(user.getCreatedAt().toString())
            .build();
    }
}
