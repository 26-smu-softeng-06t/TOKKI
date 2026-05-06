package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @Column(name = "stage_number", nullable = false)
    private Integer stageNumber;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        level = stageNumber;
        if (title == null || title.isBlank()) {
            title = defaultTitle(difficulty, stageNumber);
        }
        if (description == null || description.isBlank()) {
            description = defaultDescription(difficulty, stageNumber);
        }
    }

    public void update(DifficultyLevel difficulty, Integer stageNumber) {
        this.difficulty = difficulty;
        this.stageNumber = stageNumber;
        this.level = stageNumber;
        this.title = defaultTitle(difficulty, stageNumber);
        this.description = defaultDescription(difficulty, stageNumber);
    }

    public static String defaultTitle(DifficultyLevel difficulty, Integer stageNumber) {
        return capitalize(difficulty.name()) + " Stage " + stageNumber;
    }

    public static String defaultDescription(DifficultyLevel difficulty, Integer stageNumber) {
        return capitalize(difficulty.name()) + " level - Stage " + stageNumber;
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
