package com.tokki.service;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import com.tokki.domain.Word;
import com.tokki.dto.request.BatchStageRequest;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.request.StageWordRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.exception.AppException;
import com.tokki.repository.StageRepository;
import com.tokki.repository.WordRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

class StageServiceTest {

    private final StageRepository stageRepository = mock(StageRepository.class);
    private final WordRepository wordRepository = mock(WordRepository.class);
    private final StageService stageService = new StageService(stageRepository, wordRepository);

    @Test
    void filtersStagesByDifficultyAndStageNumber() {
        Stage stage = Stage.builder()
                .id(31L)
                .difficulty(DifficultyLevel.medium)
                .stageNumber(4)
                .level(4)
                .title("Medium Stage 4")
                .description("Medium level - Stage 4")
                .build();
        when(stageRepository.findByDifficultyAndStageNumber(DifficultyLevel.medium, 4))
                .thenReturn(Optional.of(stage));

        List<StageResponse> result = stageService.getStages("medium", 4);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDifficulty()).isEqualTo("medium");
        assertThat(result.get(0).getStageNumber()).isEqualTo(4);
        assertThat(result.get(0).getStageId()).isEqualTo(31L);
    }

    @Test
    void acceptsCanonicalDifficultyValues() {
        when(stageRepository.findByDifficulty(DifficultyLevel.easy)).thenReturn(List.of());
        when(stageRepository.findByDifficulty(DifficultyLevel.hard)).thenReturn(List.of());

        assertThat(stageService.getStages("easy", null)).isEmpty();
        assertThat(stageService.getStages("hard", null)).isEmpty();
    }

    @Test
    void rejectsDeprecatedDifficultyAliases() {
        assertThatThrownBy(() -> stageService.getStages("low", null))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 입력값");
        assertThatThrownBy(() -> stageService.getStages("high", null))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 입력값");
        assertThatThrownBy(() -> stageService.getStages("middle", null))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 입력값");
    }

    @Test
    void rejectsUnsupportedDifficulty() {
        assertThatThrownBy(() -> stageService.getStages("extreme", 1))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 입력값");
    }

    @Test
    void rejectsOutOfRangeStageNumber() {
        assertThatThrownBy(() -> stageService.getStages("easy", 11))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("잘못된 입력값");
    }

    @Test
    void toResponsesWithWordsGroupsAndOrdersWordsByStage() {
        Stage stageA = Stage.builder()
                .id(1L).difficulty(DifficultyLevel.easy).stageNumber(1)
                .level(1).title("Easy Stage 1").description("Easy level - Stage 1")
                .build();
        Stage stageB = Stage.builder()
                .id(2L).difficulty(DifficultyLevel.medium).stageNumber(1)
                .level(1).title("Medium Stage 1").description("Medium level - Stage 1")
                .build();

        // DB 글로벌 정렬(orderIndex ASC) 결과를 시뮬레이션 — 두 스테이지의 단어가 섞여서 반환됨
        Word wordA1 = Word.builder().id(1L).stage(stageA).word("apple").meaning("사과").orderIndex(1).build();
        Word wordB1 = Word.builder().id(2L).stage(stageB).word("cat").meaning("고양이").orderIndex(1).build();
        Word wordA2 = Word.builder().id(3L).stage(stageA).word("banana").meaning("바나나").orderIndex(2).build();
        Word wordB2 = Word.builder().id(4L).stage(stageB).word("dog").meaning("강아지").orderIndex(2).build();

        when(stageRepository.findAll()).thenReturn(List.of(stageA, stageB));
        when(wordRepository.findByStageIdInOrderByOrderIndexAscIdAsc(List.of(1L, 2L)))
                .thenReturn(List.of(wordA1, wordB1, wordA2, wordB2));

        List<StageResponse> result = stageService.getAllStages();

        assertThat(result).hasSize(2);
        StageResponse responseA = result.stream().filter(r -> r.getStageId().equals(1L)).findFirst().orElseThrow();
        StageResponse responseB = result.stream().filter(r -> r.getStageId().equals(2L)).findFirst().orElseThrow();

        assertThat(responseA.getWords()).hasSize(2);
        assertThat(responseA.getWords().get(0).getWord()).isEqualTo("apple");
        assertThat(responseA.getWords().get(1).getWord()).isEqualTo("banana");

        assertThat(responseB.getWords()).hasSize(2);
        assertThat(responseB.getWords().get(0).getWord()).isEqualTo("cat");
        assertThat(responseB.getWords().get(1).getWord()).isEqualTo("dog");
    }

    @Test
    @SuppressWarnings("unchecked")
    void replaceWords_updatesExistingWordInPlaceAndPreservesId() {
        Stage stage = Stage.builder()
                .id(1L).difficulty(DifficultyLevel.easy).stageNumber(1)
                .level(1).title("Easy Stage 1").description("Easy level - Stage 1")
                .build();
        Word existingWord = Word.builder()
                .id(10L).stage(stage).word("apple").meaning("사과").orderIndex(1)
                .build();

        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageRepository.existsById(1L)).thenReturn(true);
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L))
                .thenReturn(List.of(existingWord));

        CreateStageRequest request = new CreateStageRequest();
        request.setDifficulty(DifficultyLevel.easy);
        request.setStageNumber(1);
        StageWordRequest wordReq = new StageWordRequest();
        wordReq.setWord("orange");
        wordReq.setMeaning("오렌지");
        wordReq.setOrderIndex(1);
        request.setWords(List.of(wordReq));

        stageService.updateStage(1L, request);

        ArgumentCaptor<List<Word>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordRepository).saveAll(captor.capture());
        List<Word> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getId()).isEqualTo(10L);
        assertThat(saved.get(0).getWord()).isEqualTo("orange");
        verify(wordRepository, never()).deleteAll(any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void replaceWords_insertsNewWordForNewOrderIndex() {
        Stage stage = Stage.builder()
                .id(1L).difficulty(DifficultyLevel.easy).stageNumber(1)
                .level(1).title("Easy Stage 1").description("Easy level - Stage 1")
                .build();
        Word existingWord = Word.builder()
                .id(10L).stage(stage).word("apple").meaning("사과").orderIndex(1)
                .build();

        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageRepository.existsById(1L)).thenReturn(true);
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L))
                .thenReturn(List.of(existingWord));

        CreateStageRequest request = new CreateStageRequest();
        request.setDifficulty(DifficultyLevel.easy);
        request.setStageNumber(1);
        StageWordRequest keepReq = new StageWordRequest();
        keepReq.setWord("apple");
        keepReq.setMeaning("사과");
        keepReq.setOrderIndex(1);
        StageWordRequest newReq = new StageWordRequest();
        newReq.setWord("banana");
        newReq.setMeaning("바나나");
        newReq.setOrderIndex(2);
        request.setWords(List.of(keepReq, newReq));

        stageService.updateStage(1L, request);

        ArgumentCaptor<List<Word>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordRepository).saveAll(captor.capture());
        List<Word> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.stream().anyMatch(w -> Long.valueOf(10L).equals(w.getId()))).isTrue();
        assertThat(saved.stream().anyMatch(w -> w.getId() == null && "banana".equals(w.getWord()))).isTrue();
        verify(wordRepository, never()).deleteAll(any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void replaceWords_deletesWordRemovedFromIncomingList() {
        Stage stage = Stage.builder()
                .id(1L).difficulty(DifficultyLevel.easy).stageNumber(1)
                .level(1).title("Easy Stage 1").description("Easy level - Stage 1")
                .build();
        Word word1 = Word.builder()
                .id(10L).stage(stage).word("apple").meaning("사과").orderIndex(1)
                .build();
        Word word2 = Word.builder()
                .id(11L).stage(stage).word("banana").meaning("바나나").orderIndex(2)
                .build();

        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageRepository.existsById(1L)).thenReturn(true);
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L))
                .thenReturn(List.of(word1, word2));

        CreateStageRequest request = new CreateStageRequest();
        request.setDifficulty(DifficultyLevel.easy);
        request.setStageNumber(1);
        StageWordRequest keepReq = new StageWordRequest();
        keepReq.setWord("apple");
        keepReq.setMeaning("사과");
        keepReq.setOrderIndex(1);
        request.setWords(List.of(keepReq));

        stageService.updateStage(1L, request);

        ArgumentCaptor<List<Word>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        verify(wordRepository).deleteAll(deleteCaptor.capture());
        assertThat(deleteCaptor.getValue()).extracting(Word::getId).containsExactly(11L);
    }

    @Test
    void batchUpsertReturnsResponses() {
        CreateStageRequest request = new CreateStageRequest();
        request.setDifficulty(DifficultyLevel.easy);
        request.setStageNumber(1);
        StageWordRequest word = new StageWordRequest();
        word.setWord("apple");
        word.setMeaning("사과");
        word.setOrderIndex(1);
        request.setWords(List.of(word));

        Stage stage = Stage.builder()
                .id(1L)
                .difficulty(DifficultyLevel.easy)
                .stageNumber(1)
                .level(1)
                .title("Easy Stage 1")
                .description("Easy level - Stage 1")
                .build();
        when(stageRepository.findByDifficultyAndStageNumber(DifficultyLevel.easy, 1))
                .thenReturn(Optional.of(stage));
        when(stageRepository.existsById(1L)).thenReturn(true);
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L)).thenReturn(List.of());

        BatchStageRequest batchRequest = new BatchStageRequest();
        batchRequest.setStages(List.of(request));

        List<StageResponse> result = stageService.batchUpsertStages(batchRequest);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDifficulty()).isEqualTo("easy");
    }
}
