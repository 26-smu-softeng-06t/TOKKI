package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
import com.tokki.dto.response.WordRelationResponse;
import com.tokki.service.WordRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/word-relations")
@RequiredArgsConstructor
public class WordRelationController {

    private final WordRelationService wordRelationService;

    @GetMapping("/{wordId}")
    public ResponseEntity<ApiResponse<List<WordRelationResponse>>> getRelations(@PathVariable Long wordId) {
        return ResponseEntity.ok(ApiResponses.data(wordRelationService.getRelations(wordId)));
    }
}
