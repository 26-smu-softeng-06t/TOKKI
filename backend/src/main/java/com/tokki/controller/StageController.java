package com.tokki.controller;

import com.tokki.dto.request.BatchStageRequest;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.response.ExcelUploadPreviewResponse;
import com.tokki.dto.response.StageResponse;
import com.tokki.dto.response.WordResponse;
import com.tokki.service.ExcelStageUploadService;
import com.tokki.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;
    private final ExcelStageUploadService excelStageUploadService;

    @GetMapping
    public ResponseEntity<List<StageResponse>> getStages(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer stageNumber) {
        return ResponseEntity.ok(stageService.getStages(difficulty, stageNumber));
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

    @PutMapping("/{stageId}")
    public ResponseEntity<StageResponse> updateStage(
            @PathVariable Long stageId,
            @Valid @RequestBody CreateStageRequest request) {
        return ResponseEntity.ok(stageService.updateStage(stageId, request));
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<StageResponse>> batchUploadStages(@Valid @RequestBody BatchStageRequest request) {
        return ResponseEntity.ok(stageService.batchUpsertStages(request));
    }

    @PostMapping(value = "/excel/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelUploadPreviewResponse> previewExcelUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(excelStageUploadService.preview(file));
    }
}
