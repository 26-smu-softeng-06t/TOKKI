package com.tokki.repository;

import com.tokki.domain.QuizSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepository extends JpaRepository<QuizSession, String> {
    Optional<QuizSession> findByUserIdAndStageId(String userId, String stageId);
}
