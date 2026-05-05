package com.tokki.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "rankings")
public class Ranking {

    @Id
    @Column(name = "ranking_id")
    private String rankingId;

    @Column(name = "word_id", nullable = false)
    private String wordId;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String meaning;

    @Column(name = "miss_count", nullable = false)
    private int missCount;

    @Column(name = "`rank`", nullable = false)
    private int rank;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (rankingId == null || rankingId.isBlank()) {
            rankingId = UUID.randomUUID().toString();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
}
