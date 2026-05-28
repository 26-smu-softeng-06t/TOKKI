package com.tokki.dto.response;

import com.tokki.dto.request.CreateStageRequest;

import java.util.List;

public record ExcelUploadPreviewResponse(
        boolean valid,
        int rowCount,
        List<CreateStageRequest> stages,
        List<ExcelUploadRowError> errors
) {
}
