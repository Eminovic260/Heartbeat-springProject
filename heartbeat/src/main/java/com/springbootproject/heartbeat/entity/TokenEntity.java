package com.springbootproject.heartbeat.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="token")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (nullable=false)
    private int x;
    @Column(nullable = false)
    private int y;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity player;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;


    public UUID getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
}
