package com.tokki.service;

import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.dto.response.UserResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUser(String uid) {
        return userRepository.findByUid(uid)
            .map(UserResponse::from)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public void upsertUser(String uid, String email) {
        userRepository.findByUid(uid).ifPresentOrElse(
            existing -> existing.setEmail(email),
            () -> userRepository.save(User.builder()
                .uid(uid)
                .email(email)
                .role(UserRole.user)
                .createdAt(LocalDateTime.now())
                .build())
        );
    }
}
