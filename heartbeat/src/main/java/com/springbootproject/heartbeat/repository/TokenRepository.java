package com.springbootproject.heartbeat.repository;

import com.springbootproject.heartbeat.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    List<TokenEntity> findByGameId(UUID gameId);
    Optional<TokenEntity> findByGameIdAndXAndY(UUID gameId, int x, int y);
}
