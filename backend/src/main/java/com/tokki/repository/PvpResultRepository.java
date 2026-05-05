package com.tokki.repository;

import com.tokki.domain.PvpResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PvpResultRepository extends JpaRepository<PvpResult, String> {
    List<PvpResult> findByRoomRoomId(String roomId);

    int countByRoomRoomId(String roomId);
}
