package com.springbootproject.heartbeat.service;

import com.springbootproject.heartbeat.dao.GameDao;
import com.springbootproject.heartbeat.dto.GameCreationParams;
import com.springbootproject.heartbeat.model.Player;
import fr.le_campus_numerique.square_games.engine.*;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private final Map<String, GameFactory> factories = new HashMap<>();
    private final GameDao gameDao;

    public GameServiceImpl(GameDao gameDao) {
        this.gameDao = gameDao;

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
        try {
            GameFactory factory = factories.get(params.getGameType());
            if (factory == null) {
                throw new IllegalArgumentException("Game type not supported: " + params.getGameType());
            }

            List<UUID> playerIds = new ArrayList<>();
            for (int i = 0; i < params.getPlayerCount(); i++) {
                playerIds.add(UUID.randomUUID());
            }

            Game game = factory.createGameWithIds(
                UUID.randomUUID(),
                params.getBoardSize(),
                playerIds,
                new ArrayList<>(),
                new ArrayList<>()
            );

            List<Player> players = new ArrayList<>();
            char symbol = 'X';
            for (UUID playerId : playerIds) {
                players.add(new Player(playerId, String.valueOf(symbol)));
                symbol = (symbol == 'X') ? 'O' : 'X';
            }

            gameDao.createGame(game, players);
            return game.getId();
        } catch (InconsistentGameDefinitionException e) {
            throw new RuntimeException("Impossible de créer la partie", e);
        }
    }


    @Override
    public GameStatus getGameStatus(UUID gameId) {
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        return game.getStatus();
    }

    @Override
    public Collection<String> getBoard(UUID gameId) {
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

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
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        Set<CellPosition> moves = new HashSet<>();
        for (Token token : game.getRemainingTokens()) {
            moves.addAll(token.getAllowedMoves());
        }
        return moves;
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID gameId, CellPosition position) {
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        Token token = game.getBoard().get(position);
        if (token == null) return Collections.emptySet();
        return token.getAllowedMoves();
    }

    @Override
    public GameStatus moveToken(UUID gameId, CellPosition from, CellPosition to) {
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        Token token = game.getBoard().get(from);
        if (token != null) {
            try {
                token.moveTo(to);
                gameDao.updateGame(game);
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
                gameDao.addToken(gameId, token.getOwnerId().orElseThrow(), to.x(), to.y());
                gameDao.updateGame(game);
            } catch (InvalidPositionException e) {
                throw new IllegalArgumentException("Cannot place token at " + to, e);
            }
        }

        return game.getStatus();
    }

    @Override
    public GameStatus placeToken(UUID gameId, CellPosition to) {
        Game game = gameDao.getGame(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        Optional<Token> remainingTokenOpt = game.getRemainingTokens().stream()
            .filter(t -> t.getOwnerId().orElse(null).equals(game.getCurrentPlayerId()))
            .findFirst();

        if (remainingTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("No remaining token for current player");
        }

        Token token = remainingTokenOpt.get();
        try {
            token.moveTo(to);
            gameDao.addToken(gameId, token.getOwnerId().orElseThrow(), to.x(), to.y());
            gameDao.updateGame(game);
        } catch (InvalidPositionException e) {
            throw new IllegalArgumentException("Cannot place token at " + to, e);
        }

        return game.getStatus();
    }

    @Override
    public void deleteGame(UUID gameId) {
        gameDao.deleteGame(gameId);
    }
}
