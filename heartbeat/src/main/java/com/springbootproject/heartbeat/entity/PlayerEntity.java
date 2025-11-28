package com.springbootproject.heartbeat.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="player")
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name= "game_id")
    private GameEntity game;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
    @Column(nullable = false)
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
