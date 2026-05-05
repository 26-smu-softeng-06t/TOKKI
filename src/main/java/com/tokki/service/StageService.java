package com.tokki.service;

import com.tokki.domain.Stage;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.dto.response.WordResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.StageRepository;
import com.tokki.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public List<StageResponse> getAllStages() {
        return stageRepository.findAllByOrderByLevelAsc().stream()
                .map(StageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageResponse getStage(Long stageId) {
        return stageRepository.findById(stageId)
                .map(StageResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<WordResponse> getWordsByStage(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new AppException(ErrorCode.STAGE_NOT_FOUND);
        }
        return wordRepository.findByStageId(stageId).stream()
                .map(WordResponse::from)
                .toList();
    }

    @Transactional
    public StageResponse createStage(CreateStageRequest request) {
        Stage stage = stageRepository.save(Stage.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(request.getLevel())
                .build());
        return StageResponse.from(stage);
    }
}
