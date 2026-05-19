package com.tokki.repository;

import com.tokki.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByStageIdOrderByOrderIndexAscIdAsc(Long stageId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Word w WHERE w.stage.id = :stageId")
    void deleteByStageId(Long stageId);
}
