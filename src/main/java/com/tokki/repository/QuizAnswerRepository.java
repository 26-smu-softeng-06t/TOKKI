package com.tokki.repository;

import com.tokki.domain.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySessionId(Long sessionId);
}
