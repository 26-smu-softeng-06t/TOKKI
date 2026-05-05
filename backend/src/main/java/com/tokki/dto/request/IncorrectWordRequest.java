package com.tokki.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IncorrectWordRequest {
    private String incorrectWordId;
    private String progressId;
    private String wordId;
    private boolean isResolved;
}
