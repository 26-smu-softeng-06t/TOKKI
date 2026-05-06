package com.tokki.repository;

import com.tokki.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByStageIdOrderByOrderIndexAscIdAsc(Long stageId);

    void deleteByStageId(Long stageId);
}
