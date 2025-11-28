package com.springbootproject.heartbeat.repository;

import com.springbootproject.heartbeat.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {
}
