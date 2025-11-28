package com.springbootproject.heartbeat.entity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="game")
public class GameEntity {

@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
private int boardSize;
private int playerCount;

@Column(nullable = false)
private String type;

@Column(nullable = false)
    private String status;

    public UUID getId(){return id;}
    public void setId(UUID id){this.id = id;}
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }


}
