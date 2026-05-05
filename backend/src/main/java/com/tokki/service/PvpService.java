package com.tokki.service;

import com.tokki.domain.*;
import com.tokki.dto.request.CreateRoomRequest;
import com.tokki.dto.request.JoinRoomRequest;
import com.tokki.dto.request.SaveResultRequest;
import com.tokki.dto.response.PvpResultResponse;
import com.tokki.dto.response.PvpRoomResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PvpService {

    private final PvpRoomRepository pvpRoomRepository;
    private final PvpResultRepository pvpResultRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;

    @Transactional(readOnly = true)
    public List<PvpRoomResponse> getWaitingRooms() {
        return pvpRoomRepository.findByStatus("WAITING").stream()
                .map(PvpRoomResponse::from)
                .toList();
    }

    @Transactional
    public PvpRoomResponse createRoom(String uid, CreateRoomRequest request) {
        User host = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
        PvpRoom room = pvpRoomRepository.save(PvpRoom.builder()
                .hostUser(host)
                .stage(stage)
                .build());
        return PvpRoomResponse.from(room);
    }

    @Transactional
    public PvpRoomResponse joinRoom(String uid, JoinRoomRequest request) {
        User guest = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PvpRoom room = pvpRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (!"WAITING".equals(room.getStatus())) {
            throw new AppException(ErrorCode.ROOM_FULL);
        }
        room.joinGuest(guest);
        return PvpRoomResponse.from(pvpRoomRepository.save(room));
    }

    @Transactional
    public PvpResultResponse saveResult(String uid, SaveResultRequest request) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PvpRoom room = pvpRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<PvpResult> existing = pvpResultRepository.findByRoomId(room.getId());
        String result = resolveResult(request.getScore(), existing);

        PvpResult pvpResult = pvpResultRepository.save(PvpResult.builder()
                .room(room)
                .user(user)
                .score(request.getScore())
                .result(result)
                .build());

        // Update opponent's result when both results are in
        if (existing.size() == 1) {
            PvpResult opponent = existing.get(0);
            String opponentResult = result.equals("WIN") ? "LOSE" : result.equals("LOSE") ? "WIN" : "DRAW";
            // Opponent result is already saved; mark room complete
            room.complete();
            pvpRoomRepository.save(room);
        }

        return PvpResultResponse.from(pvpResult);
    }

    private String resolveResult(int score, List<PvpResult> existing) {
        if (existing.isEmpty()) return "PENDING";
        int opponentScore = existing.get(0).getScore();
        if (score > opponentScore) return "WIN";
        if (score < opponentScore) return "LOSE";
        return "DRAW";
    }
}
