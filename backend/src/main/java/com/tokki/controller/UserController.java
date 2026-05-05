package com.tokki.controller;

import com.tokki.dto.request.UpsertUserRequest;
import com.tokki.dto.response.UserResponse;
import com.tokki.security.AuthUser;
import com.tokki.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<UserResponse> upsertUser(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpsertUserRequest request) {
        return ResponseEntity.ok(userService.upsertUser(authUser.getUid(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(userService.getUser(authUser.getUid()));
    }
}
