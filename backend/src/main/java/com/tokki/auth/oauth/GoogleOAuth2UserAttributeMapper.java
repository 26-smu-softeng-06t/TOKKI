package com.tokki.auth.oauth;

import com.tokki.auth.dto.OAuth2UserAttributes;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuth2UserAttributeMapper implements OAuth2UserAttributeMapper {

    @Override
    public boolean supports(String registrationId) {
        return "google".equals(registrationId);
    }

    @Override
    public OAuth2UserAttributes map(String registrationId, Map<String, Object> attributes) {
        return new OAuth2UserAttributes(
                registrationId,
                stringAttribute(attributes, "sub"),
                stringAttribute(attributes, "email"),
                stringAttribute(attributes, "name"),
                stringAttribute(attributes, "picture")
        );
    }

    @Override
    public String nameAttributeKey() {
        return "sub";
    }

    private static String stringAttribute(Map<String, Object> attributes, String name) {
        Object value = attributes.get(name);
        return value instanceof String stringValue ? stringValue : "";
    }
}
