package com.tokki.service;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import com.tokki.dto.response.StageResponse;
import com.tokki.exception.AppException;
import com.tokki.repository.StageRepository;
import com.tokki.repository.WordRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void acceptsLowAndHighAliasesForIssueWording() {
        when(stageRepository.findByDifficulty(DifficultyLevel.easy)).thenReturn(List.of());
        when(stageRepository.findByDifficulty(DifficultyLevel.hard)).thenReturn(List.of());

        assertThat(stageService.getStages("low", null)).isEmpty();
        assertThat(stageService.getStages("high", null)).isEmpty();
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
}
