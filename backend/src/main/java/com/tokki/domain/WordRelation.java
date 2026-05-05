package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "word_relations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WordRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_word_id", nullable = false)
    private Word relatedWord;

    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;
}
