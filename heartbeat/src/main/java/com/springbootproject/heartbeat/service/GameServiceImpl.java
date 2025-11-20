package com.springbootproject.heartbeat.service;

import com.springbootproject.heartbeat.dto.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.*;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private final Map<String, GameFactory> factories = new HashMap<>();
    private final Map<UUID, Game> games = new HashMap<>();

    public GameServiceImpl() {
        TicTacToeGameFactory tttFactory = new TicTacToeGameFactory();
        ConnectFourGameFactory cfFactory = new ConnectFourGameFactory();

        factories.put(tttFactory.getGameFactoryId(), tttFactory);
        factories.put(cfFactory.getGameFactoryId(), cfFactory);
    }

    @Override
    public Collection<String> getGameIdentifiers() {
        return factories.keySet();
    }

    @Override
    public UUID createGame(GameCreationParams params) {
        GameFactory factory = factories.get(params.getGameType());
        if (factory == null) {
            throw new IllegalArgumentException("Game type not supported: " + params.getGameType());
        }

        Game game = factory.createGame(params.getPlayerCount(), params.getBoardSize());
        UUID id = UUID.randomUUID();
        games.put(id, game);
        return id;
    }

    @Override
    public GameStatus getGameStatus(UUID gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);
        return game.getStatus();
    }

    @Override
    public Collection<String> getBoard(UUID gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);

        List<String> result = new ArrayList<>();
        for (var entry : game.getBoard().entrySet()) {
            result.add("Position: " + entry.getKey() + ", Token: " + entry.getValue());
        }
        return result;
    }

    @Override
    public GameStatus endGame(UUID gameId) {
        return getGameStatus(gameId);
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);

        Set<CellPosition> moves = new HashSet<>();
        for (Token token : game.getRemainingTokens()) {
            moves.addAll(token.getAllowedMoves());
        }
        return moves;
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID gameId, CellPosition position) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);

        Token token = game.getBoard().get(position);
        if (token == null) return Collections.emptySet();

        return token.getAllowedMoves();
    }

    @Override
    public GameStatus moveToken(UUID gameId, CellPosition from, CellPosition to) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);

        Token token = game.getBoard().get(from);

        if (token != null) {
            try {
                token.moveTo(to);
            } catch (InvalidPositionException e) {
                throw new IllegalArgumentException("Cannot move token from " + from + " to " + to, e);
            }
        } else {
            Optional<Token> remainingTokenOpt = game.getRemainingTokens().stream()
                .filter(t -> t.getOwnerId().orElse(null).equals(game.getCurrentPlayerId()))
                .findFirst();

            if (remainingTokenOpt.isEmpty()) {
                throw new IllegalArgumentException("No remaining token for current player");
            }

            token = remainingTokenOpt.get();
            try {
                token.moveTo(to);
            } catch (InvalidPositionException e) {
                throw new IllegalArgumentException("Cannot place token at " + to, e);
            }
        }

        return game.getStatus();
    }

    @Override
    public GameStatus placeToken(UUID gameId, CellPosition to) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Game not found: " + gameId);

        Optional<Token> remainingTokenOpt = game.getRemainingTokens().stream()
            .filter(t -> t.getOwnerId().orElse(null).equals(game.getCurrentPlayerId()))
            .findFirst();

        if (remainingTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("No remaining token for current player");
        }

        Token token = remainingTokenOpt.get();
        try {
            token.moveTo(to);
        } catch (InvalidPositionException e) {
            throw new IllegalArgumentException("Cannot place token at " + to, e);
        }

        return game.getStatus();
    }
}
