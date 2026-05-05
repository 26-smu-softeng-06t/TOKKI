package com.tokki.repository;

import com.tokki.domain.WordRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRelationRepository extends JpaRepository<WordRelation, String> {
    List<WordRelation> findByWordId(String wordId);
}
