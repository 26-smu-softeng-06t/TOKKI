package com.tokki.service;

import com.tokki.domain.*;
import com.tokki.dto.request.SaveSessionRequest;
import com.tokki.dto.request.UpsertDraftSessionRequest;
import com.tokki.dto.response.QuizSessionResponse;
import com.tokki.exception.AppException;
import com.tokki.exception.ErrorCode;
import com.tokki.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final IncorrectWordRepository incorrectWordRepository;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;
    private final WordRepository wordRepository;

    @Transactional
    public QuizSessionResponse saveSession(String uid, SaveSessionRequest request) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));

        QuizSession session = quizSessionRepository.save(QuizSession.builder()
                .user(user)
                .stage(stage)
                .score(request.getScore())
                .totalQuestions(request.getTotalQuestions())
                .completedAt(LocalDateTime.now())
                .build());

        if (request.getAnswers() != null) {
            for (SaveSessionRequest.AnswerItem item : request.getAnswers()) {
                Word word = wordRepository.findById(item.getWordId())
                        .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_FOUND));
                quizAnswerRepository.save(QuizAnswer.builder()
                        .session(session)
                        .word(word)
                        .userAnswer(item.getUserAnswer())
                        .correct(item.getCorrect())
                        .build());
                if (Boolean.FALSE.equals(item.getCorrect())) {
                    IncorrectWord iw = incorrectWordRepository
                            .findByUserUidAndWordId(uid, word.getId())
                            .orElseGet(() -> IncorrectWord.builder().user(user).word(word).build());
                    iw.incrementCount();
                    incorrectWordRepository.save(iw);
                }
            }
        }
        return QuizSessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public List<QuizSessionResponse> getUserSessions(String uid) {
        return quizSessionRepository.findByUserUidOrderByStartedAtDesc(uid).stream()
                .map(QuizSessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuizSessionResponse getSessionById(Long sessionId, String uid) {
        QuizSession session = quizSessionRepository.findByIdWithAnswers(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getUser().getUid().equals(uid)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return QuizSessionResponse.fromWithDetails(session);
    }

    @Transactional
    public QuizSessionResponse upsertDraftSession(String uid, UpsertDraftSessionRequest request) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));

        QuizSession session = quizSessionRepository
                .findDraftSessionByUidAndStageId(uid, request.getStageId())
                .orElse(QuizSession.builder()
                        .user(user)
                        .stage(stage)
                        .build());

        session.setMode(QuizMode.valueOf(request.getMode()));
        session.setTotalQuestions(request.getTotalQuestions());
        session.updateProgress(request.getCurrentIndex(), request.getScore());

        QuizSession saved = quizSessionRepository.save(session);

        if (request.getAnswers() != null) {
            quizAnswerRepository.deleteBySessionId(saved.getId());
            for (UpsertDraftSessionRequest.AnswerItem item : request.getAnswers()) {
                Word word = wordRepository.findById(item.getWordId())
                        .orElseThrow(() -> new AppException(ErrorCode.WORD_NOT_FOUND));
                quizAnswerRepository.save(QuizAnswer.builder()
                        .session(saved)
                        .word(word)
                        .userAnswer(item.getUserAnswer())
                        .correct(item.getCorrect())
                        .build());
            }
        }

        return QuizSessionResponse.from(saved);
    }

    @Transactional
    public void deleteDraftSession(String uid, Long stageId) {
        QuizSession session = quizSessionRepository
                .findDraftSessionByUidAndStageId(uid, stageId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getUser().getUid().equals(uid)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        quizAnswerRepository.deleteBySessionId(session.getId());
        quizSessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public List<QuizSessionResponse> getDraftSessions(String uid) {
        return quizSessionRepository.findDraftSessionsByUid(uid).stream()
                .map(QuizSessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuizSessionResponse> getCompletedSessions(String uid, Long stageId) {
        List<QuizSession> sessions = stageId != null
            ? quizSessionRepository.findByUserUidOrderByStartedAtDesc(uid).stream()
                .filter(s -> s.isCompleted() && s.getStage().getId().equals(stageId))
                .toList()
            : quizSessionRepository.findCompletedSessionsByUid(uid);
        return sessions.stream()
                .map(QuizSessionResponse::from)
                .toList();
    }
}
