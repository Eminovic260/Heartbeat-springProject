package com.springbootproject.heartbeat.controller;

import com.springbootproject.heartbeat.dto.GameCreationParams;
import com.springbootproject.heartbeat.service.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/games")
    public UUID createGame(@RequestBody GameCreationParams params) {
        try {
            return gameService.createGame(params);
        } catch (InconsistentGameDefinitionException e) {
            throw new RuntimeException("Impossible de créer la partie : " + e.getMessage(), e);
        }
    }

    @GetMapping("/games")
    public Collection<String> getGames() {
        return gameService.getGameIdentifiers();
    }


    @GetMapping("/games/{gameId}/status")
    public GameStatus getGameStatus(@PathVariable UUID gameId) {
        return gameService.getGameStatus(gameId);
    }

    @GetMapping("/games/{gameId}/board")
    public Collection<String> getBoard(@PathVariable UUID gameId) {
        return gameService.getBoard(gameId);
    }

    @PostMapping("/games/{gameId}/terminate")
    public GameStatus endGame(@PathVariable UUID gameId) {
        return gameService.endGame(gameId);
    }

    @GetMapping("/games/{gameId}/tokens/at/{x}/{y}/moves")
    public Set<CellPosition> getTokenMoves(@PathVariable UUID gameId,
                                           @PathVariable int x,
                                           @PathVariable int y) {
        return gameService.getAllowedMoves(gameId, new CellPosition(x, y));
    }

    @PostMapping("/games/{gameId}/tokens/at/{x}/{y}/move")
    public GameStatus moveToken(@PathVariable UUID gameId,
                                @PathVariable int x,
                                @PathVariable int y,
                                @RequestBody CellPosition newPosition) {
        return gameService.moveToken(gameId, new CellPosition(x, y), newPosition);
    }
    @GetMapping("/games/{gameId}/tokens/moves")
    public Set<CellPosition> getAllowedMoves(@PathVariable UUID gameId) {
        return gameService.getAllowedMoves(gameId);
    }

    @PostMapping("/games/{gameId}/tokens/place")
    public GameStatus placeToken(@PathVariable UUID gameId,
                                @RequestBody CellPosition placeTo) {
        return gameService.placeToken(gameId, placeTo);
    }
}
