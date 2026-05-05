package com.tokki.controller;

import com.tokki.dto.request.UpsertUserRequest;
import com.tokki.dto.response.UserResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.security.SecurityUtils;
import com.tokki.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{uid}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String uid) {
        if (!SecurityUtils.getCurrentUid().equals(uid)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
        return ResponseEntity.ok(userService.getUser(uid));
    }

    @PostMapping
    public ResponseEntity<Void> upsertUser(@RequestBody @Valid UpsertUserRequest req) {
        if (!SecurityUtils.getCurrentUid().equals(req.getUid())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED);
        }
        userService.upsertUser(req.getUid(), req.getEmail());
        return ResponseEntity.noContent().build();
    }
}
