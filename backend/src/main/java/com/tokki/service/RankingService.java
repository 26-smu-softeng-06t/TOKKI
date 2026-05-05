package com.tokki.service;

import com.tokki.dto.response.RankingResponse;
import com.tokki.repository.RankingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final RankingRepository rankingRepository;

    public List<RankingResponse> getRankings() {
        return rankingRepository.findAllByOrderByRankAsc().stream()
            .map(RankingResponse::from)
            .toList();
    }
}
