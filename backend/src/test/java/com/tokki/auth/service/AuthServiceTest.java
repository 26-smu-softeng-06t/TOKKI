package com.tokki.auth.service;

import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.exception.AppException;
import com.tokki.repository.UserRepository;
import com.tokki.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final AuthService authService = new AuthService(userRepository, jwtProvider);

    @Test
    void rejectsAdminRegistrationWhenServerSecretIsBlank() {
        ReflectionTestUtils.setField(authService, "adminSecretKey", "");

        assertThatThrownBy(() -> authService.registerAdmin("uid-1", ""))
                .isInstanceOf(AppException.class);
    }

    @Test
    void promotesUserAndReturnsAdminTokenWhenSecretMatches() {
        ReflectionTestUtils.setField(authService, "adminSecretKey", "server-secret");
        User user = User.builder()
                .uid("uid-1")
                .email("admin@example.com")
                .nickname("Admin")
                .role(UserRole.user)
                .build();
        when(userRepository.findById("uid-1")).thenReturn(Optional.of(user));
        when(jwtProvider.generateToken("uid-1", "admin@example.com", UserRole.admin))
                .thenReturn("jwt-token");

        String token = authService.registerAdmin("uid-1", "server-secret");

        assertThat(token).isEqualTo("jwt-token");
        assertThat(user.getRole()).isEqualTo(UserRole.admin);
        verify(userRepository).save(user);
    }
}
