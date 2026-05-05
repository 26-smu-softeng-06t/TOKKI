package com.tokki.auth.oauth;

import com.tokki.auth.dto.OAuth2UserAttributes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OAuth2UserAttributeMapperRegistry {

    private final List<OAuth2UserAttributeMapper> mappers;

    public OAuth2UserAttributeMapperRegistry(List<OAuth2UserAttributeMapper> mappers) {
        this.mappers = List.copyOf(mappers);
    }

    public MappedOAuth2User map(String registrationId, Map<String, Object> attributes) {
        OAuth2UserAttributeMapper mapper = mappers.stream()
                .filter(candidate -> candidate.supports(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId));

        return new MappedOAuth2User(mapper.map(registrationId, attributes), mapper.nameAttributeKey());
    }

    public record MappedOAuth2User(OAuth2UserAttributes attributes, String nameAttributeKey) {
    }
}
