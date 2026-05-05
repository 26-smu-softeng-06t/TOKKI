package com.tokki.repository;

import com.tokki.domain.UserProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, String> {
    Optional<UserProgress> findByUserIdAndStageId(String userId, String stageId);

    List<UserProgress> findAllByUserId(String userId);
}
