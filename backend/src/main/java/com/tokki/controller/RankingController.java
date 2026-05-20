package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
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
    public ResponseEntity<ApiResponse<List<RankingResponse>>> getRankings(
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(ApiResponses.data(rankingService.getRankings(period)));
    }
}
