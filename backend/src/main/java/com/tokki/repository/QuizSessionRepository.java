package com.tokki.repository;

import com.tokki.domain.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    List<QuizSession> findByUserUidOrderByStartedAtDesc(String uid);

    @Query("SELECT s FROM QuizSession s WHERE s.user.uid = :uid AND s.completedAt IS NULL ORDER BY s.startedAt DESC")
    List<QuizSession> findDraftSessionsByUid(@Param("uid") String uid);

    @Query("SELECT s FROM QuizSession s WHERE s.user.uid = :uid AND s.stage.id = :stageId AND s.completedAt IS NULL")
    Optional<QuizSession> findDraftSessionByUidAndStageId(@Param("uid") String uid, @Param("stageId") Long stageId);

    @Query("SELECT s FROM QuizSession s LEFT JOIN FETCH s.answers a WHERE s.id = :id")
    Optional<QuizSession> findByIdWithAnswers(@Param("id") Long id);

    @Query("SELECT s FROM QuizSession s WHERE s.user.uid = :uid AND s.completedAt IS NOT NULL ORDER BY s.completedAt DESC")
    List<QuizSession> findCompletedSessionsByUid(@Param("uid") String uid);
}
