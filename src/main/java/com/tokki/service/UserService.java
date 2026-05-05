package com.tokki.service;

import com.tokki.domain.User;
import com.tokki.dto.request.UpsertUserRequest;
import com.tokki.dto.response.UserResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse upsertUser(String uid, UpsertUserRequest request) {
        User user = userRepository.findById(uid)
                .map(existing -> {
                    existing.updateNickname(request.getNickname());
                    if (request.getEmail() != null) existing.updateEmail(request.getEmail());
                    return existing;
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .uid(uid)
                        .nickname(request.getNickname())
                        .email(request.getEmail())
                        .build()));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String uid) {
        return userRepository.findById(uid)
                .map(UserResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
