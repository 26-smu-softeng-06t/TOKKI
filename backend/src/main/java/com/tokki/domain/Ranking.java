package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "rankings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"word_id", "period"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "miss_count", nullable = false)
    private Integer missCount;

    @Column(name = "rank_position", nullable = false)
    private Integer rank;

    @Column(nullable = false, length = 20)
    private String period;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    public void update(int rank, int missCount) {
        this.rank = rank;
        this.missCount = missCount;
    }
}
