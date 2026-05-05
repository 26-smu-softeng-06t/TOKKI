package com.tokki.repository;

import com.tokki.domain.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findAllByOrderByLevelAsc();
}
