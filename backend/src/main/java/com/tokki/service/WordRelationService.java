package com.tokki.service;

import com.tokki.dto.response.WordRelationResponse;
import com.tokki.repository.WordRelationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordRelationService {

    private final WordRelationRepository wordRelationRepository;

    public List<WordRelationResponse> getRelations(String wordId) {
        return wordRelationRepository.findByWordId(wordId).stream()
            .map(WordRelationResponse::from)
            .toList();
    }
}
