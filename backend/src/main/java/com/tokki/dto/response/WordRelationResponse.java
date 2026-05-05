package com.tokki.dto.response;

import com.tokki.domain.RelationType;
import com.tokki.domain.WordRelation;
import lombok.Builder;

@Builder
public record WordRelationResponse(
    String relationId,
    String wordId,
    RelationType relationType,
    String relatedWord,
    String relatedMeaning
) {
    public static WordRelationResponse from(WordRelation relation) {
        return WordRelationResponse.builder()
            .relationId(relation.getRelationId())
            .wordId(relation.getWordId())
            .relationType(relation.getRelationType())
            .relatedWord(relation.getRelatedWord())
            .relatedMeaning(relation.getRelatedMeaning())
            .build();
    }
}
