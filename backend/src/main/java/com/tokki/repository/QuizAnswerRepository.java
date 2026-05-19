package com.tokki.repository;

import com.tokki.domain.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySessionId(Long sessionId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM QuizAnswer a WHERE a.session.id = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);
}
