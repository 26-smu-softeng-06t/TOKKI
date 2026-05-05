package com.tokki.batch;

import com.tokki.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

    private final RankingRepository rankingRepository;

    // 매월 1일 자정 실행
    @Scheduled(cron = "0 0 0 1 * *")
    public void updateMonthlyRankings() {
        log.info("Monthly ranking batch started");
        // TODO: Phase 2 — 이전 달 세션 점수 집계 후 rankings 테이블 갱신
    }
}
