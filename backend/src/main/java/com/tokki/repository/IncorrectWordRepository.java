package com.tokki.repository;

import com.tokki.domain.IncorrectWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncorrectWordRepository extends JpaRepository<IncorrectWord, Long> {
    List<IncorrectWord> findByUserUidOrderByCountDesc(String uid);
    Optional<IncorrectWord> findByUserUidAndWordId(String uid, Long wordId);
}
