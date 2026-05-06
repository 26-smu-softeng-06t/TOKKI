package com.tokki.service;

import com.tokki.domain.Stage;
import com.tokki.domain.Word;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.repository.StageRepository;
import com.tokki.repository.WordRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StageServiceTest {

    private final StageRepository stageRepository = mock(StageRepository.class);
    private final WordRepository wordRepository = mock(WordRepository.class);
    private final StageService stageService = new StageService(stageRepository, wordRepository);

    @Test
    void createStageSavesWordsWithFrontendContract() {
        CreateStageRequest request = request("easy", 1, "morning", "아침");
        Stage savedStage = Stage.builder()
                .id(1L)
                .title("easy")
                .level(1)
                .build();
        when(stageRepository.save(any(Stage.class))).thenReturn(savedStage);
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L)).thenReturn(List.of());

        stageService.createStage(request);

        verify(wordRepository).save(any(Word.class));
    }

    @Test
    void updateStageUpdatesExistingWordAndAddsMissingWord() {
        Stage stage = Stage.builder()
                .id(1L)
                .title("easy")
                .level(1)
                .build();
        Word existingWord = Word.builder()
                .id(10L)
                .stage(stage)
                .word("old")
                .meaning("이전")
                .orderIndex(1)
                .build();
        CreateStageRequest request = request("medium", 2, "new", "새로운");
        request.getWords().add(word("second", "두번째", 2));

        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(wordRepository.findByStageIdOrderByOrderIndexAscIdAsc(1L)).thenReturn(List.of(existingWord));

        stageService.updateStage(1L, request);

        assertThat(stage.getTitle()).isEqualTo("medium");
        assertThat(stage.getLevel()).isEqualTo(2);
        assertThat(existingWord.getWord()).isEqualTo("new");
        assertThat(existingWord.getMeaning()).isEqualTo("새로운");
        verify(wordRepository, times(1)).save(any(Word.class));
    }

    @Test
    void deleteStageDeletesWordsBeforeStage() {
        when(stageRepository.existsById(1L)).thenReturn(true);

        stageService.deleteStage(1L);

        verify(wordRepository).deleteByStageId(1L);
        verify(stageRepository).deleteById(1L);
    }

    private CreateStageRequest request(String difficulty, int stageNumber, String word, String meaning) {
        CreateStageRequest request = new CreateStageRequest();
        request.setDifficulty(difficulty);
        request.setStageNumber(stageNumber);
        request.setWords(new ArrayList<>(List.of(word(word, meaning, 1))));
        return request;
    }

    private CreateStageRequest.WordItem word(String word, String meaning, int orderIndex) {
        CreateStageRequest.WordItem item = new CreateStageRequest.WordItem();
        item.setWord(word);
        item.setMeaning(meaning);
        item.setOrderIndex(orderIndex);
        return item;
    }
}
