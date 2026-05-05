package com.tokki.repository;

import com.tokki.domain.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserUid(String uid);
    Optional<UserProgress> findByUserUidAndStageId(String uid, Long stageId);
}
