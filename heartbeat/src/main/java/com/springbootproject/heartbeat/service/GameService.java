package com.springbootproject.heartbeat.service;

import com.springbootproject.heartbeat.dto.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.*;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface GameService {
    Collection<String> getGameIdentifiers();
    UUID createGame(GameCreationParams params);
    GameStatus getGameStatus(UUID gameId);
    Collection<String> getBoard(UUID gameId);
    GameStatus endGame(UUID gameId);
    Set<CellPosition> getAllowedMoves(UUID gameId, CellPosition position);

    Set<CellPosition> getAllowedMoves(UUID gameId);

    GameStatus moveToken(UUID gameId, CellPosition from, CellPosition to);

    GameStatus placeToken(UUID gameId, CellPosition to);
}
