package com.tokki.repository;

import com.tokki.domain.IncorrectWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncorrectWordRepository extends JpaRepository<IncorrectWord, Long> {
    List<IncorrectWord> findByUserUidOrderByCountDesc(String uid);
    Optional<IncorrectWord> findByUserUidAndWordId(String uid, Long wordId);

    @Query("SELECT new com.tokki.repository.WordMissCountDto(i.word, SUM(i.count)) " +
           "FROM IncorrectWord i " +
           "GROUP BY i.word " +
           "ORDER BY SUM(i.count) DESC")
    List<WordMissCountDto> aggregateMissCountsByWord();
}
