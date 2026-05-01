package com.tokki.auth.dto;

import java.util.Map;

public record OAuth2UserAttributes(
    String provider,
    String providerId,
    String email,
    String name,
    String picture
) {
    public static OAuth2UserAttributes of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(attributes);
        }
        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
    }

    private static OAuth2UserAttributes ofGoogle(Map<String, Object> attributes) {
        return new OAuth2UserAttributes(
            "google",
            (String) attributes.get("sub"),
            (String) attributes.get("email"),
            (String) attributes.get("name"),
            (String) attributes.get("picture")
        );
    }
}