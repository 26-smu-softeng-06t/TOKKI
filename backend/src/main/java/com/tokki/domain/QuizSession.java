package com.tokki.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "quiz_sessions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stage_id"})
)
public class QuizSession {

    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "stage_id", nullable = false)
    private String stageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizMode mode;

    @Builder.Default
    @Column(name = "current_index", nullable = false)
    private int currentIndex = 0;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @Builder.Default
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }
        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        savedAt = LocalDateTime.now();
    }
}
