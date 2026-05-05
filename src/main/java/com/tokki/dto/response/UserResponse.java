package com.tokki.dto.response;

import com.tokki.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private String uid;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .uid(user.getUid())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
