package com.tokki.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    name = "pvp_results",
    uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"})
)
public class PvpResult {

    @Id
    @Column(name = "result_id")
    private String resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private PvpRoom room;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private int score;

    @Column(name = "completion_time", nullable = false)
    private float completionTime;

    @Column(name = "is_winner", nullable = false)
    private boolean isWinner;

    @PrePersist
    void prePersist() {
        if (resultId == null || resultId.isBlank()) {
            resultId = UUID.randomUUID().toString();
        }
    }
}
