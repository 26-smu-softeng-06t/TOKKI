package com.tokki.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "pvp_rooms")
public class PvpRoom {

    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "invite_code", nullable = false, unique = true, length = 6)
    private String inviteCode;

    @Column(name = "host_user_id", nullable = false)
    private String hostUserId;

    @Column(name = "guest_user_id")
    private String guestUserId;

    @Column(name = "stage_id", nullable = false)
    private String stageId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PvpStatus status = PvpStatus.waiting;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<PvpResult> results = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (roomId == null || roomId.isBlank()) {
            roomId = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = PvpStatus.waiting;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
