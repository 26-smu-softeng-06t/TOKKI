package com.tokki.batch;

import com.tokki.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

    private final RankingService rankingService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateDailyRankings() {
        log.info("Daily ranking batch started - updating top 10 incorrect words");
        try {
            rankingService.updateDailyRankings();
            log.info("Daily ranking batch completed successfully");
        } catch (Exception e) {
            log.error("Daily ranking batch failed", e);
        }
    }
}
