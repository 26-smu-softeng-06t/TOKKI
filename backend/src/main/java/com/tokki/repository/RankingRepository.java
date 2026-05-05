package com.tokki.repository;

import com.tokki.domain.Ranking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingRepository extends JpaRepository<Ranking, String> {
    List<Ranking> findAllByOrderByRankAsc();
}
