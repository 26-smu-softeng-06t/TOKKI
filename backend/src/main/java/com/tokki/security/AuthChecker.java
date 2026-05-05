package com.tokki.security;

import com.tokki.domain.UserRole;
import com.tokki.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChecker {

    private final UserRepository userRepository;

    public boolean isAdmin(Authentication auth) {
        if (!(auth instanceof AuthUser authUser)) {
            return false;
        }
        return userRepository.findByUid(authUser.getUid())
            .map(user -> user.getRole() == UserRole.admin)
            .orElse(false);
    }
}
