package com.tokki.repository;

import com.tokki.domain.PvpResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PvpResultRepository extends JpaRepository<PvpResult, Long> {
    List<PvpResult> findByRoomId(Long roomId);
    List<PvpResult> findByUserUid(String uid);
}
