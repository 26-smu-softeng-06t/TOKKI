package com.tokki.auth.service;

import com.tokki.auth.dto.OAuth2UserAttributes;
import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.UserRepository;
import com.tokki.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${tokki.admin.secret-key:}")
    private String adminSecretKey;

    @Transactional
    public User upsertOAuth2User(OAuth2UserAttributes attributes) {
        String uid = attributes.providerId();
        User user = userRepository.findById(uid)
                .orElseGet(() -> User.builder()
                        .uid(uid)
                        .nickname(displayName(attributes))
                        .email(attributes.email())
                        .role(UserRole.user)
                        .build());

        user.updateEmail(attributes.email());
        user.updateNickname(displayName(attributes));
        return userRepository.save(user);
    }

    @Transactional
    public User registerAdmin(String uid, String providedSecretKey) {
        if (!StringUtils.hasText(adminSecretKey) || !adminSecretKey.equals(providedSecretKey)) {
            throw new AppException(ErrorCode.ADMIN_SECRET_INVALID);
        }

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(UserRole.admin);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String issueToken(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return jwtProvider.generateToken(user.getUid(), user.getEmail(), user.getRole());
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(String uid) {
        return userRepository.findById(uid);
    }

    private static String displayName(OAuth2UserAttributes attributes) {
        String name;
        if (StringUtils.hasText(attributes.name())) {
            name = attributes.name();
        } else if (StringUtils.hasText(attributes.email())) {
            name = attributes.email();
        } else {
            name = attributes.providerId();
        }
        return name.length() > 50 ? name.substring(0, 50) : name;
    }
}
