package com.tokki.service;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import com.tokki.domain.Word;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.request.WordRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.StageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StageService {

    private final StageRepository stageRepository;

    @Transactional(readOnly = true)
    public List<StageResponse> getStages(DifficultyLevel difficulty) {
        List<Stage> stages = difficulty == null
            ? stageRepository.findAll()
            : stageRepository.findAllByDifficultyOrderByStageNumberAsc(difficulty);
        return stages.stream().map(StageResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StageResponse getStage(String stageId) {
        return stageRepository.findById(stageId)
            .map(StageResponse::from)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public StageResponse createStage(CreateStageRequest req) {
        stageRepository.findByDifficultyAndStageNumber(req.getDifficulty(), req.getStageNumber())
            .ifPresent(stage -> {
                throw new AppException(ErrorCode.ALREADY_EXISTS);
            });
        Stage stage = Stage.builder()
            .stageId(req.getStageId())
            .difficulty(req.getDifficulty())
            .stageNumber(req.getStageNumber())
            .build();
        addWords(stage, req.getWords());
        return StageResponse.from(stageRepository.save(stage));
    }

    public StageResponse updateStage(String stageId, CreateStageRequest req) {
        Stage stage = stageRepository.findById(stageId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        stage.setDifficulty(req.getDifficulty());
        stage.setStageNumber(req.getStageNumber());
        stage.getWords().clear();
        addWords(stage, req.getWords());
        return StageResponse.from(stageRepository.save(stage));
    }

    public void deleteStage(String stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        stageRepository.deleteById(stageId);
    }

    public void batchCreate(List<CreateStageRequest> requests) {
        requests.forEach(this::createStage);
    }

    private void addWords(Stage stage, List<WordRequest> requests) {
        requests.forEach(req -> {
            Word word = Word.builder()
                .wordId(req.getWordId())
                .stage(stage)
                .word(req.getWord())
                .meaning(req.getMeaning())
                .example(req.getExample())
                .orderIndex(req.getOrderIndex())
                .build();
            stage.getWords().add(word);
        });
    }
}
