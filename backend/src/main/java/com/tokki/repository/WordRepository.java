package com.tokki.repository;

import com.tokki.domain.Word;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, String> {
    List<Word> findAllByStageStageIdOrderByOrderIndexAsc(String stageId);
}
