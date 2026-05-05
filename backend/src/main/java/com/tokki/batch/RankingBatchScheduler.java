package com.tokki.batch;

import com.tokki.domain.Ranking;
import com.tokki.repository.IncorrectWordRepository;
import com.tokki.repository.RankingRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

    private final IncorrectWordRepository incorrectWordRepository;
    private final RankingRepository rankingRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void refreshRankings() {
        List<Object[]> top10 = incorrectWordRepository.findTop10MissedWords(PageRequest.of(0, 10));
        rankingRepository.deleteAll();

        List<Ranking> rankings = new ArrayList<>();
        for (int i = 0; i < top10.size(); i++) {
            Object[] row = top10.get(i);
            rankings.add(Ranking.builder()
                .wordId((String) row[0])
                .word((String) row[1])
                .meaning((String) row[2])
                .missCount(((Long) row[3]).intValue())
                .rank(i + 1)
                .updatedAt(LocalDateTime.now())
                .build());
        }
        rankingRepository.saveAll(rankings);
        log.info("[Batch] 랭킹 갱신 완료: {} 건", rankings.size());
    }
}
