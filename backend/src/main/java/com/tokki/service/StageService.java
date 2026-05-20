package com.tokki.service;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import com.tokki.domain.Word;
import com.tokki.dto.request.BatchStageRequest;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.request.StageWordRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.dto.response.WordResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.StageRepository;
import com.tokki.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public List<StageResponse> getAllStages() {
        return toResponsesWithWords(stageRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<StageResponse> getStages(String difficultyValue, Integer stageNumber) {
        DifficultyLevel difficulty = parseDifficulty(difficultyValue);
        validateStageNumber(stageNumber, false);

        if (difficulty == null && stageNumber == null) {
            return getAllStages();
        }
        if (difficulty == null) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        List<Stage> stages = stageNumber == null
                ? stageRepository.findByDifficulty(difficulty)
                : stageRepository.findByDifficultyAndStageNumber(difficulty, stageNumber)
                        .map(List::of).orElse(List.of());
        return toResponsesWithWords(stages);
    }

    private List<StageResponse> toResponsesWithWords(List<Stage> stages) {
        if (stages.isEmpty()) return List.of();
        List<Long> ids = stages.stream().map(Stage::getId).toList();
        Map<Long, List<WordResponse>> wordsByStage = wordRepository
                .findByStageIdInOrderByOrderIndexAscIdAsc(ids)
                .stream()
                .collect(Collectors.groupingBy(
                        w -> w.getStage().getId(),
                        Collectors.mapping(WordResponse::from, Collectors.toList())
                ));
        return stages.stream()
                .sorted(stageComparator())
                .map(s -> StageResponse.from(s, wordsByStage.getOrDefault(s.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public StageResponse getStage(Long stageId) {
        Stage stage = getStageEntity(stageId);
        List<WordResponse> words = getWordsByStage(stageId);
        return StageResponse.from(stage, words);
    }

    @Transactional(readOnly = true)
    public List<WordResponse> getWordsByStage(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new AppException(ErrorCode.STAGE_NOT_FOUND);
        }
        return wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(stageId).stream()
                .map(WordResponse::from)
                .toList();
    }

    @Transactional
    public StageResponse createStage(CreateStageRequest request) {
        return upsertStage(request);
    }

    @Transactional
    public StageResponse updateStage(Long stageId, CreateStageRequest request) {
        validateStageRequest(request);
        Stage stage = getStageEntity(stageId);
        stage.update(request.getDifficulty(), request.getStageNumber());
        replaceWords(stage, request.getWords());
        return StageResponse.from(stage, getWordsByStage(stage.getId()));
    }

    @Transactional
    public void deleteStage(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new AppException(ErrorCode.STAGE_NOT_FOUND);
        }
        wordRepository.deleteByStageId(stageId);
        stageRepository.deleteById(stageId);
    }

    @Transactional
    public List<StageResponse> batchUpsertStages(BatchStageRequest request) {
        return request.getStages().stream()
                .map(this::upsertStage)
                .toList();
    }

    private StageResponse upsertStage(CreateStageRequest request) {
        validateStageRequest(request);
        Stage stage = stageRepository.findByDifficultyAndStageNumber(
                        request.getDifficulty(),
                        request.getStageNumber())
                .orElseGet(() -> stageRepository.save(Stage.builder()
                        .difficulty(request.getDifficulty())
                        .stageNumber(request.getStageNumber())
                        .level(request.getStageNumber())
                        .title(Stage.defaultTitle(request.getDifficulty(), request.getStageNumber()))
                        .description(Stage.defaultDescription(request.getDifficulty(), request.getStageNumber()))
                        .build()));
        stage.update(request.getDifficulty(), request.getStageNumber());
        replaceWords(stage, request.getWords());
        return StageResponse.from(stage, getWordsByStage(stage.getId()));
    }

    private void replaceWords(Stage stage, List<StageWordRequest> words) {
        wordRepository.deleteByStageId(stage.getId());
        if (words == null || words.isEmpty()) {
            return;
        }
        List<Word> newWords = words.stream()
                .sorted(Comparator.comparing(StageWordRequest::getOrderIndex))
                .map(word -> Word.builder()
                        .stage(stage)
                        .word(word.getWord())
                        .meaning(word.getMeaning())
                        .example(word.getExample())
                        .imageUrl(word.getImageUrl())
                        .orderIndex(word.getOrderIndex())
                        .build())
                .toList();
        wordRepository.saveAll(newWords);
    }

    private Stage getStageEntity(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
    }

    private void validateStageRequest(CreateStageRequest request) {
        if (request.getDifficulty() == null) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        validateStageNumber(request.getStageNumber(), true);
        if (request.getWords() != null && request.getWords().size() > 10) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    private DifficultyLevel parseDifficulty(String difficultyValue) {
        try {
            return DifficultyLevel.from(difficultyValue);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    private void validateStageNumber(Integer stageNumber, boolean required) {
        if (stageNumber == null) {
            if (required) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            return;
        }
        if (stageNumber < 1 || stageNumber > 10) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    private Comparator<Stage> stageComparator() {
        return Comparator
                .comparing(Stage::getDifficulty)
                .thenComparing(Stage::getStageNumber);
    }
}
