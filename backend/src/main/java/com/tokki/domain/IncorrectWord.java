package com.tokki.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "incorrect_words")
public class IncorrectWord {

    @Id
    @Column(name = "incorrect_word_id")
    private String incorrectWordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", nullable = false)
    private UserProgress progress;

    @Column(name = "word_id", nullable = false)
    private String wordId;

    @Builder.Default
    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @PrePersist
    void prePersist() {
        if (incorrectWordId == null || incorrectWordId.isBlank()) {
            incorrectWordId = UUID.randomUUID().toString();
        }
    }
}
