package com.tokki.controller;

import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.dto.response.WordResponse;
import com.tokki.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @GetMapping
    public ResponseEntity<List<StageResponse>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<StageResponse> getStage(@PathVariable Long stageId) {
        return ResponseEntity.ok(stageService.getStage(stageId));
    }

    @GetMapping("/{stageId}/words")
    public ResponseEntity<List<WordResponse>> getWords(@PathVariable Long stageId) {
        return ResponseEntity.ok(stageService.getWordsByStage(stageId));
    }

    @PostMapping
    public ResponseEntity<StageResponse> createStage(@Valid @RequestBody CreateStageRequest request) {
        return ResponseEntity.ok(stageService.createStage(request));
    }
}
