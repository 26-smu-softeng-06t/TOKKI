package com.tokki.repository;

import com.tokki.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findByPeriodOrderByRankAsc(String period);

    void deleteByPeriod(String period);

    List<Ranking> findByPeriodOrderByMissCountDesc(String period);

    boolean existsByWordIdAndPeriod(Long wordId, String period);
}
