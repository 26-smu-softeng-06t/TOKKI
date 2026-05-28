package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
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
    public ResponseEntity<ApiResponse<UserResponse>> upsertUser(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpsertUserRequest request) {
        return ResponseEntity.ok(ApiResponses.data(userService.upsertUser(authUser.getUid(), request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(ApiResponses.data(userService.getUser(authUser.getUid())));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable String uid,
            @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(ApiResponses.data(userService.getUser(uid, authUser.getUid(), authUser.getRole())));
    }
}
