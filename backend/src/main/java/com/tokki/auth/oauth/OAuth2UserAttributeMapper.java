package com.tokki.auth.oauth;

import com.tokki.auth.dto.OAuth2UserAttributes;

import java.util.Map;

public interface OAuth2UserAttributeMapper {

    boolean supports(String registrationId);

    OAuth2UserAttributes map(String registrationId, Map<String, Object> attributes);

    String nameAttributeKey();
}
