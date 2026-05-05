package com.tokki.service;

import com.tokki.domain.IncorrectWord;
import com.tokki.domain.UserProgress;
import com.tokki.dto.request.SaveProgressRequest;
import com.tokki.dto.response.IncorrectWordResponse;
import com.tokki.dto.response.ProgressResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.IncorrectWordRepository;
import com.tokki.repository.UserProgressRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final IncorrectWordRepository incorrectWordRepository;

    @Transactional(readOnly = true)
    public ProgressResponse getProgress(String userId, String stageId) {
        return userProgressRepository.findByUserIdAndStageId(userId, stageId)
            .map(ProgressResponse::from)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public void saveProgress(SaveProgressRequest req) {
        UserProgress progress = userProgressRepository.findById(req.getProgressId() == null ? "" : req.getProgressId())
            .orElseGet(() -> UserProgress.builder()
                .progressId(req.getProgressId())
                .userId(req.getUserId())
                .stageId(req.getStageId())
                .build());
        progress.setCompleted(req.isCompleted());
        progress.setLastScore(req.getLastScore());
        progress.setUpdatedAt(LocalDateTime.now());
        progress.getIncorrectWords().clear();
        req.getIncorrectWords().forEach(iwr -> progress.getIncorrectWords().add(IncorrectWord.builder()
            .incorrectWordId(iwr.getIncorrectWordId())
            .progress(progress)
            .wordId(iwr.getWordId())
            .isResolved(iwr.isResolved())
            .build()));
        userProgressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public List<IncorrectWordResponse> getIncorrectWords(String userId) {
        return incorrectWordRepository.findUnresolvedByUserId(userId).stream()
            .map(IncorrectWordResponse::from)
            .toList();
    }

    public void resolveIncorrectWord(String progressId, String wordId) {
        IncorrectWord iw = incorrectWordRepository.findByProgressProgressIdAndWordId(progressId, wordId)
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        iw.setResolved(true);
    }
}
