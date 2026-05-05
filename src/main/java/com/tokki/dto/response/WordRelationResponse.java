package com.tokki.dto.response;

import com.tokki.domain.WordRelation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WordRelationResponse {
    private Long id;
    private Long wordId;
    private Long relatedWordId;
    private String relationType;

    public static WordRelationResponse from(WordRelation relation) {
        return WordRelationResponse.builder()
                .id(relation.getId())
                .wordId(relation.getWord().getId())
                .relatedWordId(relation.getRelatedWord().getId())
                .relationType(relation.getRelationType())
                .build();
    }
}
