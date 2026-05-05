package com.tokki.repository;

import com.tokki.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findByPeriodOrderByRankAsc(String period);
    Optional<Ranking> findByUserUidAndPeriod(String uid, String period);
}
