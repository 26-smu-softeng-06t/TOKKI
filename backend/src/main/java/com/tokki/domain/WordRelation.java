package com.tokki.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "word_relations")
public class WordRelation {

    @Id
    @Column(name = "relation_id")
    private String relationId;

    @Column(name = "word_id", nullable = false)
    private String wordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false)
    private RelationType relationType;

    @Column(name = "related_word", nullable = false)
    private String relatedWord;

    @Column(name = "related_meaning", nullable = false)
    private String relatedMeaning;

    @PrePersist
    void prePersist() {
        if (relationId == null || relationId.isBlank()) {
            relationId = UUID.randomUUID().toString();
        }
    }
}
