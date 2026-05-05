package com.tokki.repository;

import com.tokki.domain.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    List<QuizSession> findByUserUidOrderByStartedAtDesc(String uid);
}
