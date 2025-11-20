package com.springbootproject.heartbeat.dto;

public class GameCreationParams {
    private String gameType;
    private int playerCount;
    private int boardSize;

    // constructeur par défaut (pour Jackson)
    public GameCreationParams() {}

    public GameCreationParams(String gameType, int playerCount, int boardSize) {
        this.gameType = gameType;
        this.playerCount = playerCount;
        this.boardSize = boardSize;
    }

    // Getters
    public String getGameType() {
        return gameType;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getBoardSize() {
        return boardSize;
    }

    // Setters
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }
}
