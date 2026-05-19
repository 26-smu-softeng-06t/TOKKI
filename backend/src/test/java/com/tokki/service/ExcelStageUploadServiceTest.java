package com.tokki.service;

import com.tokki.dto.response.ExcelUploadPreviewResponse;
import com.tokki.exception.AppException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelStageUploadServiceTest {

    private final ExcelStageUploadService service = new ExcelStageUploadService();

    @Test
    void rejectsNullFile() {
        assertThatThrownBy(() -> service.preview(null))
                .isInstanceOf(AppException.class);
    }

    @Test
    void rejectsEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        assertThatThrownBy(() -> service.preview(emptyFile))
                .isInstanceOf(AppException.class);
    }

    @Test
    void rejectsNonXlsxFile() {
        MultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        assertThatThrownBy(() -> service.preview(csvFile))
                .isInstanceOf(AppException.class);
    }

    @Test
    void rejectsFileOver5MB() {
        byte[] largeContent = new byte[5 * 1024 * 1024 + 1];
        MultipartFile largeFile = new MockMultipartFile("file", "large.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", largeContent);
        assertThatThrownBy(() -> service.preview(largeFile))
                .isInstanceOf(AppException.class);
    }

    @Test
    void detectsMissingRequiredHeaders() throws IOException {
        Path tempFile = Files.createTempFile("test", ".xlsx");
        Files.write(tempFile, new byte[0]);

        MultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Files.readAllBytes(tempFile));
        Files.delete(tempFile);

        assertThatThrownBy(() -> service.preview(file))
                .isInstanceOf(AppException.class);
    }

    @Test
    void returnsErrorForDuplicateOrderIndexWithinSameStage() {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMockExcelContentWithDuplicateOrderIndex()
        );

        ExcelUploadPreviewResponse result = service.preview(mockFile);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).anyMatch(error ->
                error.field().equals("orderIndex") && error.message().contains("중복")
        );
    }

    @Test
    void validatesSuccessfulPreview() {
        MultipartFile validFile = new MockMultipartFile(
                "file",
                "valid.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMockValidExcelContent()
        );

        ExcelUploadPreviewResponse result = service.preview(validFile);

        assertThat(result.valid()).isTrue();
        assertThat(result.errors()).isEmpty();
        assertThat(result.rowCount()).isGreaterThan(0);
        assertThat(result.stages()).isNotEmpty();
    }

    private byte[] createMockExcelContentWithDuplicateOrderIndex() {
        return createWorkbookBytes(new Object[][]{
                {"difficulty", "stageNumber", "orderIndex", "word", "meaning"},
                {"easy", 1, 1, "apple", "사과"},
                {"easy", 1, 1, "banana", "바나나"}
        });
    }

    private byte[] createMockValidExcelContent() {
        return createWorkbookBytes(new Object[][]{
                {"difficulty", "stageNumber", "orderIndex", "word", "meaning", "example", "imageUrl"},
                {"easy", 1, 1, "apple", "사과", "I eat an apple.", "https://example.com/apple.png"},
                {"easy", 1, 2, "banana", "바나나", "", ""}
        });
    }

    private byte[] createWorkbookBytes(Object[][] rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("words");
            for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                Object[] values = rows[rowIndex];
                for (int cellIndex = 0; cellIndex < values.length; cellIndex++) {
                    Object value = values[cellIndex];
                    if (value instanceof Number number) {
                        row.createCell(cellIndex).setCellValue(number.doubleValue());
                    } else {
                        row.createCell(cellIndex).setCellValue(String.valueOf(value));
                    }
                }
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("Failed to create workbook fixture", e);
        }
    }
}
