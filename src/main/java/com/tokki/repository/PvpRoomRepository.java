package com.tokki.repository;

import com.tokki.domain.PvpRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PvpRoomRepository extends JpaRepository<PvpRoom, Long> {
    List<PvpRoom> findByStatus(String status);
    List<PvpRoom> findByHostUserUidOrGuestUserUid(String hostUid, String guestUid);
}
