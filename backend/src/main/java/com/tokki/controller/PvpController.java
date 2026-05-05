package com.tokki.controller;

import com.tokki.dto.request.CreateRoomRequest;
import com.tokki.dto.request.JoinRoomRequest;
import com.tokki.dto.request.SaveResultRequest;
import com.tokki.dto.response.PvpRoomResponse;
import com.tokki.security.SecurityUtils;
import com.tokki.service.PvpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pvp")
@RequiredArgsConstructor
public class PvpController {

    private final PvpService pvpService;

    @PostMapping("/rooms")
    public ResponseEntity<PvpRoomResponse> createRoom(@RequestBody @Valid CreateRoomRequest req) {
        String hostUserId = SecurityUtils.getCurrentUid();
        return ResponseEntity.status(HttpStatus.CREATED).body(pvpService.createRoom(hostUserId, req.getStageId()));
    }

    @PostMapping("/rooms/join")
    public ResponseEntity<PvpRoomResponse> joinRoom(@RequestBody @Valid JoinRoomRequest req) {
        String guestUserId = SecurityUtils.getCurrentUid();
        return ResponseEntity.ok(pvpService.joinRoom(req.getInviteCode(), guestUserId));
    }

    @PostMapping("/rooms/{roomId}/results")
    public ResponseEntity<Void> saveResult(@PathVariable String roomId, @RequestBody @Valid SaveResultRequest req) {
        String userId = SecurityUtils.getCurrentUid();
        pvpService.saveResult(roomId, userId, req.getScore(), req.getCompletionTime());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rooms/{roomId}/complete")
    public ResponseEntity<Void> completeRoom(@PathVariable String roomId) {
        pvpService.completeRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
