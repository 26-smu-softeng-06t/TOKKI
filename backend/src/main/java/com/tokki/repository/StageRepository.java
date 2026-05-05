package com.tokki.repository;

import com.tokki.domain.DifficultyLevel;
import com.tokki.domain.Stage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, String> {
    Optional<Stage> findByDifficultyAndStageNumber(DifficultyLevel difficulty, int stageNumber);

    List<Stage> findAllByDifficultyOrderByStageNumberAsc(DifficultyLevel difficulty);
}
