package com.tokki.security;

import com.tokki.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUser {
    private final String uid;
    private final String email;
    private final UserRole role;
}