package com.tokki.repository;

import com.tokki.domain.Stage;
import com.tokki.domain.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findAllByOrderByLevelAsc();

    List<Stage> findByDifficulty(DifficultyLevel difficulty);

    Optional<Stage> findByDifficultyAndStageNumber(DifficultyLevel difficulty, Integer stageNumber);
}
