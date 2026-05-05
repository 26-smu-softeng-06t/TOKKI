package com.tokki.auth.service;

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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${tokki.admin.secret-key:}")
    private String adminSecretKey;

    @Transactional
    public String registerAdmin(String uid, String providedSecretKey) {
        if (!StringUtils.hasText(adminSecretKey) || !adminSecretKey.equals(providedSecretKey)) {
            throw new AppException(ErrorCode.ADMIN_SECRET_INVALID);
        }

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(UserRole.admin);
        userRepository.save(user);

        return jwtProvider.generateToken(user.getUid(), user.getEmail(), user.getRole());
    }

    @Transactional(readOnly = true)
    public String issueToken(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return jwtProvider.generateToken(user.getUid(), user.getEmail(), user.getRole());
    }
}
