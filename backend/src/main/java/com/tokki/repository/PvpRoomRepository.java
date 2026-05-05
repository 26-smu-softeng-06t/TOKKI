package com.tokki.repository;

import com.tokki.domain.PvpRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PvpRoomRepository extends JpaRepository<PvpRoom, String> {
    Optional<PvpRoom> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
