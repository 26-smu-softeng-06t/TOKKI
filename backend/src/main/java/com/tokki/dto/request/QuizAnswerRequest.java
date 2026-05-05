package com.tokki.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizAnswerRequest {
    private String answerId;
    private String wordId;
    private String userAnswer;
    private boolean isCorrect;
}
