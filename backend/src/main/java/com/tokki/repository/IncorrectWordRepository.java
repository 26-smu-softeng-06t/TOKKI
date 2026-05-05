package com.tokki.repository;

import com.tokki.domain.IncorrectWord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncorrectWordRepository extends JpaRepository<IncorrectWord, String> {

    @Query("""
        SELECT iw FROM IncorrectWord iw
        JOIN iw.progress up
        WHERE up.userId = :userId AND iw.isResolved = false
        """)
    List<IncorrectWord> findUnresolvedByUserId(@Param("userId") String userId);

    Optional<IncorrectWord> findByProgressProgressIdAndWordId(String progressId, String wordId);

    @Query("""
        SELECT iw.wordId, w.word, w.meaning, COUNT(iw) AS missCount
        FROM IncorrectWord iw
        JOIN Word w ON iw.wordId = w.wordId
        GROUP BY iw.wordId, w.word, w.meaning
        ORDER BY missCount DESC
        """)
    List<Object[]> findTop10MissedWords(Pageable pageable);
}
