package com.springbootproject.heartbeat.repository;

import com.springbootproject.heartbeat.entity.PlayerEntity;
import com.springbootproject.heartbeat.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    List<PlayerEntity> findByGame(GameEntity game);
    List<PlayerEntity> findByGameId(UUID gameId);
}
