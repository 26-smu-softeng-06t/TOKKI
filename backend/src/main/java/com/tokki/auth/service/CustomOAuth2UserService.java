package com.tokki.auth.service;

import com.tokki.auth.oauth.OAuth2UserAttributeMapperRegistry;
import com.tokki.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserAttributeMapperRegistry attributeMapperRegistry;
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserAttributeMapperRegistry.MappedOAuth2User mappedUser =
                attributeMapperRegistry.map(registrationId, attributes);

        log.info("[OAuth2] Provider={}, ProviderId={}, Email={}",
                mappedUser.attributes().provider(),
                mappedUser.attributes().providerId(),
                mappedUser.attributes().email());

        User user = authService.upsertOAuth2User(mappedUser.attributes());

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())),
                attributes,
                mappedUser.nameAttributeKey()
        );
    }
}
