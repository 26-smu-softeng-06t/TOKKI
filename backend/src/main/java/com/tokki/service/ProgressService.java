package com.tokki.service;

import com.tokki.domain.*;
import com.tokki.dto.request.SaveProgressRequest;
import com.tokki.dto.response.IncorrectWordResponse;
import com.tokki.dto.response.ProgressResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.IncorrectWordRepository;
import com.tokki.repository.StageRepository;
import com.tokki.repository.UserProgressRepository;
import com.tokki.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final IncorrectWordRepository incorrectWordRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;

    @Transactional(readOnly = true)
    public List<ProgressResponse> getUserProgress(String uid) {
        return userProgressRepository.findByUserUid(uid).stream()
                .map(ProgressResponse::from)
                .toList();
    }

    @Transactional
    public ProgressResponse saveProgress(String uid, SaveProgressRequest request) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));

        UserProgress progress = userProgressRepository
                .findByUserUidAndStageId(uid, request.getStageId())
                .orElseGet(() -> UserProgress.builder()
                        .user(user)
                        .stage(stage)
                        .build());

        if (Boolean.TRUE.equals(request.getCompleted())) {
            progress.complete();
        }
        return ProgressResponse.from(userProgressRepository.save(progress));
    }

    @Transactional(readOnly = true)
    public List<IncorrectWordResponse> getIncorrectWords(String uid) {
        return incorrectWordRepository.findByUserUidOrderByCountDesc(uid).stream()
                .map(IncorrectWordResponse::from)
                .toList();
    }

    @Transactional
    public void deleteIncorrectWord(String uid, Long wordId) {
        IncorrectWord incorrectWord = incorrectWordRepository.findByUserUidAndWordId(uid, wordId)
                .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_FOUND));
        incorrectWordRepository.delete(incorrectWord);
    }
}
