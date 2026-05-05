package com.tokki.repository;

import com.tokki.domain.WordRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRelationRepository extends JpaRepository<WordRelation, Long> {
    List<WordRelation> findByWordId(Long wordId);
}
