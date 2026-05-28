package com.tokki.controller;

import com.tokki.common.api.ApiResponse;
import com.tokki.common.api.ApiResponses;
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
    public ResponseEntity<ApiResponse<List<StageResponse>>> getStages(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer stageNumber) {
        return ResponseEntity.ok(ApiResponses.data(stageService.getStages(difficulty, stageNumber)));
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<ApiResponse<StageResponse>> getStage(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponses.data(stageService.getStage(stageId)));
    }

    @GetMapping("/{stageId}/words")
    public ResponseEntity<ApiResponse<List<WordResponse>>> getWords(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponses.data(stageService.getWordsByStage(stageId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StageResponse>> createStage(@Valid @RequestBody CreateStageRequest request) {
        return ResponseEntity.ok(ApiResponses.data(stageService.createStage(request)));
    }

    @PutMapping("/{stageId}")
    public ResponseEntity<ApiResponse<StageResponse>> updateStage(
            @PathVariable Long stageId,
            @Valid @RequestBody CreateStageRequest request) {
        return ResponseEntity.ok(ApiResponses.data(stageService.updateStage(stageId, request)));
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<StageResponse>>> batchUploadStages(@Valid @RequestBody BatchStageRequest request) {
        return ResponseEntity.ok(ApiResponses.data(stageService.batchUpsertStages(request)));
    }

    @PostMapping(value = "/excel/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ExcelUploadPreviewResponse>> previewExcelUpload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponses.data(excelStageUploadService.preview(file)));
    }
}
