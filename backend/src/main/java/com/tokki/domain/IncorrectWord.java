package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incorrect_words", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uid", "word_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IncorrectWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Builder.Default
    @Column(nullable = false)
    private Integer count = 0;

    @Column(name = "last_incorrect_at")
    private LocalDateTime lastIncorrectAt;

    public void incrementCount() {
        this.count++;
        this.lastIncorrectAt = LocalDateTime.now();
    }
}
