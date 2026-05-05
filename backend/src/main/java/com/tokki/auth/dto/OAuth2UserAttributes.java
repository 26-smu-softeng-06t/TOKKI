package com.tokki.auth.dto;

public record OAuth2UserAttributes(
    String provider,
    String providerId,
    String email,
    String name,
    String picture
) {
}
