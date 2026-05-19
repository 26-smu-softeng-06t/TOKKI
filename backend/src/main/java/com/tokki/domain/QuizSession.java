package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 10)
    private QuizMode mode;

    @Column(name = "current_index")
    private Integer currentIndex;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizAnswer> answers = new ArrayList<>();

    public boolean isDraft() {
        return completedAt == null;
    }

    public boolean isCompleted() {
        return completedAt != null;
    }

    public void markCompleted() {
        this.completedAt = LocalDateTime.now();
    }

    public void updateProgress(int newIndex, int newScore) {
        this.currentIndex = newIndex;
        this.score = newScore;
    }

    public void setMode(QuizMode mode) {
        this.mode = mode;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        if (currentIndex == null) {
            currentIndex = 0;
        }
        if (score == null) {
            score = 0;
        }
    }
}
