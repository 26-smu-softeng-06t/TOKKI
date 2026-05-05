package com.tokki.controller;

import com.tokki.dto.request.CreateRoomRequest;
import com.tokki.dto.request.JoinRoomRequest;
import com.tokki.dto.request.SaveResultRequest;
import com.tokki.dto.response.PvpResultResponse;
import com.tokki.dto.response.PvpRoomResponse;
import com.tokki.security.AuthUser;
import com.tokki.service.PvpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pvp")
@RequiredArgsConstructor
public class PvpController {

    private final PvpService pvpService;

    @GetMapping("/rooms")
    public ResponseEntity<List<PvpRoomResponse>> getWaitingRooms() {
        return ResponseEntity.ok(pvpService.getWaitingRooms());
    }

    @PostMapping("/rooms")
    public ResponseEntity<PvpRoomResponse> createRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(pvpService.createRoom(authUser.getUid(), request));
    }

    @PostMapping("/rooms/join")
    public ResponseEntity<PvpRoomResponse> joinRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody JoinRoomRequest request) {
        return ResponseEntity.ok(pvpService.joinRoom(authUser.getUid(), request));
    }

    @PostMapping("/results")
    public ResponseEntity<PvpResultResponse> saveResult(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody SaveResultRequest request) {
        return ResponseEntity.ok(pvpService.saveResult(authUser.getUid(), request));
    }
}
