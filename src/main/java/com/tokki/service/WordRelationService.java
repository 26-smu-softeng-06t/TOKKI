package com.tokki.service;

import com.tokki.dto.response.WordRelationResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.WordRelationRepository;
import com.tokki.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordRelationService {

    private final WordRelationRepository wordRelationRepository;
    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public List<WordRelationResponse> getRelations(Long wordId) {
        if (!wordRepository.existsById(wordId)) {
            throw new AppException(ErrorCode.WORD_NOT_FOUND);
        }
        return wordRelationRepository.findByWordId(wordId).stream()
                .map(WordRelationResponse::from)
                .toList();
    }
}
