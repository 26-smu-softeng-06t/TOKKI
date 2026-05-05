package com.tokki.service;

import com.tokki.dto.response.RankingResponse;
import com.tokki.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    @Transactional(readOnly = true)
    public List<RankingResponse> getRankings(String period) {
        String target = (period != null && !period.isBlank())
                ? period
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return rankingRepository.findByPeriodOrderByRankAsc(target).stream()
                .map(RankingResponse::from)
                .toList();
    }
}
