package com.tokki.dto.response;

public record ExcelUploadRowError(
        int rowNumber,
        String field,
        String message
) {
}
