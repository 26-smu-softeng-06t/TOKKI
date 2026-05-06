package com.tokki.service;

import com.tokki.domain.User;
import com.tokki.domain.UserRole;
import com.tokki.dto.request.UpsertUserRequest;
import com.tokki.exception.AppException;
import com.tokki.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    @Test
    void upsertPreservesExistingRole() {
        User existingAdmin = User.builder()
                .uid("admin-uid")
                .nickname("Old")
                .email("old@example.com")
                .role(UserRole.admin)
                .build();
        UpsertUserRequest request = new UpsertUserRequest();
        request.setNickname("New");
        request.setEmail("new@example.com");
        when(userRepository.findById("admin-uid")).thenReturn(Optional.of(existingAdmin));

        userService.upsertUser("admin-uid", request);

        assertThat(existingAdmin.getNickname()).isEqualTo("New");
        assertThat(existingAdmin.getEmail()).isEqualTo("new@example.com");
        assertThat(existingAdmin.getRole()).isEqualTo(UserRole.admin);
        verify(userRepository, never()).save(existingAdmin);
    }

    @Test
    void ownerCanReadSelf() {
        User owner = User.builder()
                .uid("uid-1")
                .nickname("Owner")
                .email("owner@example.com")
                .role(UserRole.user)
                .build();
        when(userRepository.findById("uid-1")).thenReturn(Optional.of(owner));

        assertThat(userService.getUser("uid-1", "uid-1", UserRole.user).getUid()).isEqualTo("uid-1");
    }

    @Test
    void adminCanReadOtherUser() {
        User user = User.builder()
                .uid("uid-2")
                .nickname("User")
                .email("user@example.com")
                .role(UserRole.user)
                .build();
        when(userRepository.findById("uid-2")).thenReturn(Optional.of(user));

        assertThat(userService.getUser("uid-2", "admin-uid", UserRole.admin).getUid()).isEqualTo("uid-2");
    }

    @Test
    void userCannotReadOtherUser() {
        assertThatThrownBy(() -> userService.getUser("uid-2", "uid-1", UserRole.user))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("접근 권한");
    }
}
