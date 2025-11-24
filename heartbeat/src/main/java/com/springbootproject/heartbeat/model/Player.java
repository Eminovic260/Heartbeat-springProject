package com.springbootproject.heartbeat.model;

import java.util.UUID;

public class Player {
    private UUID id;
    private String symbol;

    public Player(UUID id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }
    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

}
