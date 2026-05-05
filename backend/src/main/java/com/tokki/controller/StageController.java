package com.tokki.controller;

import com.tokki.domain.DifficultyLevel;
import com.tokki.dto.request.BatchUploadRequest;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.response.StageResponse;
import com.tokki.service.StageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @GetMapping
    public ResponseEntity<List<StageResponse>> getStages(@RequestParam(required = false) DifficultyLevel difficulty) {
        return ResponseEntity.ok(stageService.getStages(difficulty));
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<StageResponse> getStage(@PathVariable String stageId) {
        return ResponseEntity.ok(stageService.getStage(stageId));
    }

    @PostMapping
    @PreAuthorize("@authChecker.isAdmin(authentication)")
    public ResponseEntity<StageResponse> createStage(@RequestBody @Valid CreateStageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stageService.createStage(req));
    }

    @PostMapping("/batch")
    @PreAuthorize("@authChecker.isAdmin(authentication)")
    public ResponseEntity<Void> batchUpload(@RequestBody @Valid BatchUploadRequest req) {
        stageService.batchCreate(req.getStages());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{stageId}")
    @PreAuthorize("@authChecker.isAdmin(authentication)")
    public ResponseEntity<StageResponse> updateStage(@PathVariable String stageId, @RequestBody @Valid CreateStageRequest req) {
        return ResponseEntity.ok(stageService.updateStage(stageId, req));
    }

    @DeleteMapping("/{stageId}")
    @PreAuthorize("@authChecker.isAdmin(authentication)")
    public ResponseEntity<Void> deleteStage(@PathVariable String stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }
}
