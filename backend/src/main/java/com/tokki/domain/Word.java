package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "words")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(nullable = false, length = 200)
    private String meaning;

    @Column(length = 500)
    private String example;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void update(String word, String meaning, String example, String imageUrl, Integer orderIndex) {
        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.imageUrl = imageUrl;
        this.orderIndex = orderIndex;
    }
}
