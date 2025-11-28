package com.springbootproject.heartbeat.controller;

import com.springbootproject.heartbeat.dto.GameCreationParams;
import com.springbootproject.heartbeat.service.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // ----------------------------- CREATE GAME -----------------------------
    @Operation(
        summary = "Create a new game",
        description = "Creates a game of type Taquin, TicTacToe, ConnectFour, etc.",
        tags = {"Games"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Game successfully created",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid game configuration",
            content = @Content(schema = @Schema()))
    })
    @PostMapping
    public UUID createGame(@RequestBody GameCreationParams params) {
        try {
            return gameService.createGame(params);
        } catch (InconsistentGameDefinitionException e) {
            throw new RuntimeException("Impossible de créer la partie : " + e.getMessage(), e);
        }
    }

    // ----------------------------- LIST GAMES -----------------------------
    @Operation(
        summary = "List all active game identifiers",
        tags = {"Games"}
    )
    @GetMapping
    public Collection<String> getGames() {
        return gameService.getGameIdentifiers();
    }

    // ----------------------------- GAME STATUS -----------------------------
    @Operation(
        summary = "Get game status",
        description = "Returns the current status of a game (RUNNING, FINISHED, etc.)",
        tags = {"Games"}
    )
    @GetMapping("/{gameId}/status")
    public GameStatus getGameStatus(
        @Parameter(description = "UUID of the game") @PathVariable UUID gameId) {
        return gameService.getGameStatus(gameId);
    }

    // ----------------------------- BOARD STATE -----------------------------
    @Operation(
        summary = "Get game board",
        description = "Returns the board of the selected game",
        tags = {"Board"}
    )
    @GetMapping("/{gameId}/board")
    public Collection<String> getBoard(
        @Parameter(description = "UUID of the game") @PathVariable UUID gameId) {
        return gameService.getBoard(gameId);
    }

    // ----------------------------- TERMINATE GAME -----------------------------
    @Operation(
        summary = "Terminate a game",
        description = "Stops the selected game and returns its final status",
        tags = {"Games"}
    )
    @PostMapping("/{gameId}/terminate")
    public GameStatus endGame(
        @Parameter(description = "UUID of the game") @PathVariable UUID gameId) {
        return gameService.endGame(gameId);
    }

    // ----------------------------- GET TOKEN MOVES -----------------------------
    @Operation(
        summary = "Get allowed moves for a token",
        description = "Returns all possible moves for the token located at (x, y)",
        tags = {"Tokens"}
    )
    @GetMapping("/{gameId}/tokens/at/{x}/{y}/moves")
    public Set<CellPosition> getTokenMoves(
        @PathVariable UUID gameId,
        @Parameter(description = "X coordinate") @PathVariable int x,
        @Parameter(description = "Y coordinate") @PathVariable int y) {
        return gameService.getAllowedMoves(gameId, new CellPosition(x, y));
    }

    // ----------------------------- MOVE TOKEN -----------------------------
    @Operation(
        summary = "Move a token",
        description = "Moves a token from (x, y) to the provided new position",
        tags = {"Tokens"}
    )
    @PostMapping("/{gameId}/tokens/at/{x}/{y}/move")
    public GameStatus moveToken(
        @PathVariable UUID gameId,
        @Parameter(description = "X coordinate") @PathVariable int x,
        @Parameter(description = "Y coordinate") @PathVariable int y,
        @RequestBody CellPosition newPosition) {
        return gameService.moveToken(gameId, new CellPosition(x, y), newPosition);
    }

    // ----------------------------- ALL ALLOWED MOVES -----------------------------
    @Operation(
        summary = "Get all allowed moves in the game",
        tags = {"Tokens"}
    )
    @GetMapping("/{gameId}/tokens/moves")
    public Set<CellPosition> getAllowedMoves(
        @Parameter(description = "UUID of the game") @PathVariable UUID gameId) {
        return gameService.getAllowedMoves(gameId);
    }

    // ----------------------------- PLACE TOKEN -----------------------------
    @Operation(
        summary = "Place a token",
        description = "Places a token at the chosen position",
        tags = {"Tokens"}
    )
    @PostMapping("/{gameId}/tokens/place")
    public GameStatus placeToken(
        @PathVariable UUID gameId,
        @RequestBody CellPosition placeTo) {
        return gameService.placeToken(gameId, placeTo);
    }
}
