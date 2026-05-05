package com.tokki.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveResultRequest {
    @PositiveOrZero
    private int score;

    @PositiveOrZero
    private float completionTime;
}
