package com.tokki.service;

import com.tokki.domain.PvpResult;
import com.tokki.domain.PvpRoom;
import com.tokki.domain.PvpStatus;
import com.tokki.dto.response.PvpRoomResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.PvpResultRepository;
import com.tokki.repository.PvpRoomRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PvpService {

    private final PvpRoomRepository pvpRoomRepository;
    private final PvpResultRepository pvpResultRepository;

    public PvpRoomResponse createRoom(String hostUserId, String stageId) {
        PvpRoom room = PvpRoom.builder()
            .inviteCode(generateUniqueInviteCode())
            .hostUserId(hostUserId)
            .stageId(stageId)
            .status(PvpStatus.waiting)
            .createdAt(LocalDateTime.now())
            .build();
        return PvpRoomResponse.from(pvpRoomRepository.save(room));
    }

    public PvpRoomResponse joinRoom(String inviteCode, String guestUserId) {
        PvpRoom room = pvpRoomRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new AppException(ErrorCode.INVITE_CODE_NOT_FOUND));
        if (room.getGuestUserId() != null) {
            throw new AppException(ErrorCode.ROOM_FULL);
        }
        room.setGuestUserId(guestUserId);
        room.setStatus(PvpStatus.in_progress);
        return PvpRoomResponse.from(room);
    }

    public void saveResult(String roomId, String userId, int score, float completionTime) {
        PvpRoom room = pvpRoomRepository.findById(roomId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        pvpResultRepository.save(PvpResult.builder()
            .room(room)
            .userId(userId)
            .score(score)
            .completionTime(completionTime)
            .isWinner(false)
            .build());
    }

    public void updateWinner(String roomId, String winnerId) {
        pvpResultRepository.findByRoomRoomId(roomId).forEach(result -> result.setWinner(result.getUserId().equals(winnerId)));
    }

    public void completeRoom(String roomId) {
        PvpRoom room = pvpRoomRepository.findById(roomId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        room.setStatus(PvpStatus.completed);
    }

    @Transactional(readOnly = true)
    public String determineWinnerId(String roomId) {
        List<PvpResult> results = pvpResultRepository.findByRoomRoomId(roomId);
        if (results.size() < 2) {
            throw new AppException(ErrorCode.INVALID_ARGUMENT, "결과가 2개 이상 필요합니다");
        }
        return results.stream()
            .max(Comparator.comparingInt(PvpResult::getScore)
                .thenComparing(Comparator.comparing(PvpResult::getCompletionTime).reversed()))
            .map(PvpResult::getUserId)
            .orElseThrow(() -> new AppException(ErrorCode.INTERNAL));
    }

    private String generateUniqueInviteCode() {
        for (int i = 0; i < 5; i++) {
            String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            if (!pvpRoomRepository.existsByInviteCode(code)) {
                return code;
            }
        }
        throw new AppException(ErrorCode.INTERNAL, "초대 코드 생성 실패");
    }
}
