package com.tokki.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uid", "period"})
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
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "rank_position", nullable = false)
    private Integer rank;

    @Column(nullable = false, length = 20)
    private String period;

    public void update(int rank, int score) {
        this.rank = rank;
        this.score = score;
    }
}
