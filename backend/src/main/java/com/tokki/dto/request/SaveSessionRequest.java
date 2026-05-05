package com.tokki.dto.request;

import com.tokki.domain.QuizMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveSessionRequest {
    private String sessionId;

    @NotBlank
    private String userId;

    @NotBlank
    private String stageId;

    @NotNull
    private QuizMode mode;

    private int currentIndex;

    @Valid
    private List<QuizAnswerRequest> answers = new ArrayList<>();
}
