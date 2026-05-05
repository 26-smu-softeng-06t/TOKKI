package com.tokki.controller;

import com.tokki.dto.response.RankingResponse;
import com.tokki.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingResponse>> getRankings(
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(rankingService.getRankings(period));
    }
}
