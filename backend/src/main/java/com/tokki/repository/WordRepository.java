package com.tokki.repository;

import com.tokki.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByStageIdOrderByOrderIndexAscIdAsc(Long stageId);

    @Query("SELECT w FROM Word w WHERE w.stage.id IN :stageIds ORDER BY w.orderIndex ASC, w.id ASC")
    List<Word> findByStageIdInOrderByOrderIndexAscIdAsc(@Param("stageIds") List<Long> stageIds);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Word w WHERE w.stage.id = :stageId")
    void deleteByStageId(Long stageId);
}
