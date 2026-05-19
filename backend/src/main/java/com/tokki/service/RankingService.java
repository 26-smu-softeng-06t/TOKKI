package com.tokki.service;

import com.tokki.domain.Ranking;
import com.tokki.dto.response.RankingResponse;
import com.tokki.repository.IncorrectWordRepository;
import com.tokki.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final IncorrectWordRepository incorrectWordRepository;

    private static final int TOP_N = 10;

    @Transactional(readOnly = true)
    public List<RankingResponse> getRankings(String period) {
        String target = (period != null && !period.isBlank())
                ? period
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return rankingRepository.findByPeriodOrderByRankAsc(target).stream()
                .map(RankingResponse::from)
                .toList();
    }

    @Transactional
    public void updateDailyRankings() {
        LocalDate today = LocalDate.now();
        String period = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        var incorrectWordCounts = incorrectWordRepository.aggregateMissCountsByWord();
        rankingRepository.deleteByPeriod(period);

        List<Ranking> newRankings = new ArrayList<>();
        int rank = 1;
        for (var entry : incorrectWordCounts) {
            if (rank > TOP_N) break;

            int totalMissCount = entry.getTotalCount().intValue();

            Ranking ranking = Ranking.builder()
                    .word(entry.getWord())
                    .missCount(totalMissCount)
                    .rank(rank)
                    .period(period)
                    .snapshotDate(today)
                    .build();

            newRankings.add(ranking);
            rank++;
        }

        rankingRepository.saveAll(newRankings);
    }
}
