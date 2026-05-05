package com.tokki.controller;

import com.tokki.dto.response.WordRelationResponse;
import com.tokki.service.WordRelationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/word-relations")
@RequiredArgsConstructor
public class WordRelationController {

    private final WordRelationService wordRelationService;

    @GetMapping("/{wordId}")
    public ResponseEntity<List<WordRelationResponse>> getRelations(@PathVariable String wordId) {
        return ResponseEntity.ok(wordRelationService.getRelations(wordId));
    }
}
