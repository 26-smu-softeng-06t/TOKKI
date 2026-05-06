package com.tokki.service;

import com.tokki.domain.Stage;
import com.tokki.domain.Word;
import com.tokki.dto.request.BatchUploadRequest;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(stageId).stream()
                .map(WordResponse::from)
                .toList();
    }

    @Transactional
    public StageResponse createStage(CreateStageRequest request) {
        Stage stage = stageRepository.save(Stage.builder()
                .title(request.resolvedTitle())
                .description(request.getDescription())
                .level(request.resolvedLevel())
                .build());
        saveWords(stage, request.getWords());
        return StageResponse.from(stage);
    }

    @Transactional
    public StageResponse updateStage(Long stageId, CreateStageRequest request) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
        stage.update(request.resolvedTitle(), request.getDescription(), request.resolvedLevel());
        saveWords(stage, request.getWords());
        return StageResponse.from(stage);
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
    public List<StageResponse> batchUpload(BatchUploadRequest request) {
        return request.getStages().stream()
                .map(this::createStage)
                .toList();
    }

    private void saveWords(Stage stage, List<CreateStageRequest.WordItem> wordItems) {
        Map<Integer, Word> existingByOrder = wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(stage.getId()).stream()
                .collect(Collectors.toMap(Word::getOrderIndex, Function.identity(), (first, second) -> first));
        List<Integer> requestedOrders = wordItems.stream()
                .map(CreateStageRequest.WordItem::getOrderIndex)
                .toList();

        for (CreateStageRequest.WordItem item : wordItems) {
            Word existing = existingByOrder.get(item.getOrderIndex());
            if (existing == null) {
                wordRepository.save(Word.builder()
                        .stage(stage)
                        .word(item.getWord())
                        .meaning(item.getMeaning())
                        .example(item.getExample())
                        .imageUrl(item.getImageUrl())
                        .orderIndex(item.getOrderIndex())
                        .build());
            } else {
                existing.update(item.getWord(), item.getMeaning(), item.getExample(), item.getImageUrl(), item.getOrderIndex());
            }
        }

        existingByOrder.values().stream()
                .filter(word -> !requestedOrders.contains(word.getOrderIndex()))
                .forEach(wordRepository::delete);
    }
}
