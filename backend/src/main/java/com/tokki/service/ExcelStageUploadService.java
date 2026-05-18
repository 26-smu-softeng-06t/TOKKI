package com.tokki.service;

import com.tokki.domain.DifficultyLevel;
import com.tokki.dto.request.CreateStageRequest;
import com.tokki.dto.request.StageWordRequest;
import com.tokki.dto.response.ExcelUploadPreviewResponse;
import com.tokki.dto.response.ExcelUploadRowError;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class ExcelStageUploadService {

    private static final long MAX_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "difficulty", "stagenumber", "orderindex", "word", "meaning"
    );

    public ExcelUploadPreviewResponse preview(MultipartFile file) {
        validateFile(file);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }

            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> headers = readHeaders(sheet.getRow(0), formatter);
            List<ExcelUploadRowError> errors = new ArrayList<>(validateHeaders(headers));
            Map<String, StageDraft> drafts = new LinkedHashMap<>();
            int rowCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isBlankRow(row, formatter)) {
                    continue;
                }
                rowCount++;
                readRow(row, i + 1, headers, formatter, drafts, errors);
            }

            List<CreateStageRequest> stages = drafts.values().stream()
                    .map(StageDraft::toRequest)
                    .sorted(Comparator.comparing(CreateStageRequest::getDifficulty)
                            .thenComparing(CreateStageRequest::getStageNumber))
                    .toList();
            if (rowCount == 0) {
                errors.add(new ExcelUploadRowError(1, "file", "업로드할 데이터 행이 없습니다."));
            }

            return new ExcelUploadPreviewResponse(errors.isEmpty(), rowCount, stages, errors);
        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!filename.endsWith(".xlsx") || file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    private Map<String, Integer> readHeaders(Row headerRow, DataFormatter formatter) {
        Map<String, Integer> headers = new HashMap<>();
        if (headerRow == null) {
            return headers;
        }
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String header = normalizeHeader(formatter.formatCellValue(headerRow.getCell(i)));
            if (!header.isBlank()) {
                headers.put(header, i);
            }
        }
        return headers;
    }

    private List<ExcelUploadRowError> validateHeaders(Map<String, Integer> headers) {
        List<ExcelUploadRowError> errors = new ArrayList<>();
        for (String required : REQUIRED_HEADERS) {
            if (!headers.containsKey(required)) {
                errors.add(new ExcelUploadRowError(1, required, "필수 컬럼이 없습니다."));
            }
        }
        return errors;
    }

    private void readRow(Row row, int rowNumber, Map<String, Integer> headers, DataFormatter formatter,
                         Map<String, StageDraft> drafts, List<ExcelUploadRowError> errors) {
        String difficultyValue = value(row, headers, formatter, "difficulty");
        String stageNumberValue = value(row, headers, formatter, "stagenumber");
        String orderIndexValue = value(row, headers, formatter, "orderindex");
        String word = value(row, headers, formatter, "word");
        String meaning = value(row, headers, formatter, "meaning");
        String example = value(row, headers, formatter, "example");
        String imageUrl = value(row, headers, formatter, "imageurl");

        DifficultyLevel difficulty = parseDifficulty(difficultyValue, rowNumber, errors);
        Integer stageNumber = parseBoundedNumber(stageNumberValue, rowNumber, "stageNumber", errors);
        Integer orderIndex = parseBoundedNumber(orderIndexValue, rowNumber, "orderIndex", errors);
        requireText(word, rowNumber, "word", errors);
        requireText(meaning, rowNumber, "meaning", errors);

        if (difficulty == null || stageNumber == null || orderIndex == null || word.isBlank() || meaning.isBlank()) {
            return;
        }

        String key = difficulty.name() + ":" + stageNumber;
        StageDraft draft = drafts.computeIfAbsent(key, ignored -> new StageDraft(difficulty, stageNumber));
        if (!draft.orderIndexes.add(orderIndex)) {
            errors.add(new ExcelUploadRowError(rowNumber, "orderIndex", "같은 스테이지 안에서 orderIndex가 중복되었습니다."));
            return;
        }
        draft.words.add(toWordRequest(word, meaning, example, imageUrl, orderIndex));
    }

    private StageWordRequest toWordRequest(String word, String meaning, String example, String imageUrl, Integer orderIndex) {
        StageWordRequest request = new StageWordRequest();
        request.setWord(word);
        request.setMeaning(meaning);
        request.setExample(example.isBlank() ? null : example);
        request.setImageUrl(imageUrl.isBlank() ? null : imageUrl);
        request.setOrderIndex(orderIndex);
        return request;
    }

    private DifficultyLevel parseDifficulty(String value, int rowNumber, List<ExcelUploadRowError> errors) {
        try {
            return DifficultyLevel.from(value);
        } catch (IllegalArgumentException e) {
            errors.add(new ExcelUploadRowError(rowNumber, "difficulty", "easy, medium, hard 중 하나여야 합니다."));
            return null;
        }
    }

    private Integer parseBoundedNumber(String value, int rowNumber, String field, List<ExcelUploadRowError> errors) {
        try {
            int number = Integer.parseInt(value);
            if (number < 1 || number > 10) {
                errors.add(new ExcelUploadRowError(rowNumber, field, "1부터 10 사이의 숫자여야 합니다."));
                return null;
            }
            return number;
        } catch (NumberFormatException e) {
            errors.add(new ExcelUploadRowError(rowNumber, field, "숫자여야 합니다."));
            return null;
        }
    }

    private void requireText(String value, int rowNumber, String field, List<ExcelUploadRowError> errors) {
        if (value.isBlank()) {
            errors.add(new ExcelUploadRowError(rowNumber, field, "필수값입니다."));
        }
    }

    private String value(Row row, Map<String, Integer> headers, DataFormatter formatter, String header) {
        Integer index = headers.get(header);
        if (index == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(index)).trim();
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (!formatter.formatCellValue(row.getCell(i)).trim().isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String normalizeHeader(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private record StageDraft(DifficultyLevel difficulty, Integer stageNumber, List<StageWordRequest> words,
                              Set<Integer> orderIndexes) {
        StageDraft(DifficultyLevel difficulty, Integer stageNumber) {
            this(difficulty, stageNumber, new ArrayList<>(), new TreeSet<>());
        }

        CreateStageRequest toRequest() {
            CreateStageRequest request = new CreateStageRequest();
            request.setDifficulty(difficulty);
            request.setStageNumber(stageNumber);
            request.setWords(words.stream()
                    .sorted(Comparator.comparing(StageWordRequest::getOrderIndex))
                    .toList());
            return request;
        }
    }
}
