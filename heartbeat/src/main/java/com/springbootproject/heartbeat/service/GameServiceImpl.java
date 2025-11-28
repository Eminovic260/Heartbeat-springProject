package com.springbootproject.heartbeat.service;

import com.springbootproject.heartbeat.dto.GameCreationParams;
import com.springbootproject.heartbeat.entity.GameEntity;
import com.springbootproject.heartbeat.entity.PlayerEntity;
import com.springbootproject.heartbeat.entity.TokenEntity;
import com.springbootproject.heartbeat.repository.GameRepository;
import com.springbootproject.heartbeat.repository.PlayerRepository;
import com.springbootproject.heartbeat.repository.TokenRepository;
import fr.le_campus_numerique.square_games.engine.*;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final Map<String, GameFactory> factories = new HashMap<>();

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TokenRepository tokenRepository;

    public GameServiceImpl(GameRepository gameRepository,
                           PlayerRepository playerRepository,
                           TokenRepository tokenRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.tokenRepository = tokenRepository;

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

            GameEntity gameEntity = new GameEntity();
            gameEntity.setId(game.getId());
            gameEntity.setType(params.getGameType());
            gameEntity.setBoardSize(params.getBoardSize());
            gameEntity.setPlayerCount(params.getPlayerCount());
            gameEntity.setStatus("ONGOING");
            gameRepository.save(gameEntity);

            char symbol = 'X';
            for (UUID playerId : playerIds) {
                PlayerEntity playerEntity = new PlayerEntity();
                playerEntity.setId(playerId);
                playerEntity.setGame(gameEntity);
                playerEntity.setSymbol(String.valueOf(symbol));
                playerRepository.save(playerEntity);
                symbol = (symbol == 'X') ? 'O' : 'X';
            }

            return game.getId();

        } catch (InconsistentGameDefinitionException e) {
            throw new RuntimeException("Impossible de créer la partie", e);
        }
    }

    @Override
    public GameStatus getGameStatus(UUID gameId) {
        GameEntity game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        return GameStatus.ONGOING;
    }

    @Override
    public Collection<String> getBoard(UUID gameId) {
        List<TokenEntity> tokens = tokenRepository.findByGameId(gameId);
        return tokens.stream()
            .map(t -> "Position: (" + t.getX() + "," + t.getY() + "), Owner: " + t.getPlayer().getSymbol())
            .collect(Collectors.toList());
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID gameId) {
        List<TokenEntity> tokens = tokenRepository.findByGameId(gameId);
        Set<CellPosition> moves = new HashSet<>();
        for (TokenEntity token : tokens) {
            moves.add(new CellPosition(token.getX() + 1, token.getY()));
            moves.add(new CellPosition(token.getX() - 1, token.getY()));
            moves.add(new CellPosition(token.getX(), token.getY() + 1));
            moves.add(new CellPosition(token.getX(), token.getY() - 1));
        }
        return moves;
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID gameId, CellPosition position) {
        TokenEntity token = tokenRepository.findByGameIdAndXAndY(gameId, position.x(), position.y())
            .orElseThrow(() -> new IllegalArgumentException("Token not found at " + position));

        Set<CellPosition> moves = new HashSet<>();
        moves.add(new CellPosition(token.getX() + 1, token.getY()));
        moves.add(new CellPosition(token.getX() - 1, token.getY()));
        moves.add(new CellPosition(token.getX(), token.getY() + 1));
        moves.add(new CellPosition(token.getX(), token.getY() - 1));
        return moves;
    }

    @Override
    public GameStatus moveToken(UUID gameId, CellPosition from, CellPosition to) {
        TokenEntity token = tokenRepository.findByGameIdAndXAndY(gameId, from.x(), from.y())
            .orElseThrow(() -> new IllegalArgumentException("Token not found at " + from));

        token.setX(to.x());
        token.setY(to.y());
        tokenRepository.save(token);

        return GameStatus.ONGOING;
    }

    @Override
    public GameStatus placeToken(UUID gameId, CellPosition to) {
        GameEntity game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        PlayerEntity player = playerRepository.findByGameId(gameId).get(0); // premier joueur
        TokenEntity token = new TokenEntity();
        token.setGame(game);
        token.setPlayer(player);
        token.setX(to.x());
        token.setY(to.y());
        tokenRepository.save(token);

        return GameStatus.ONGOING;
    }

    @Override
    public void deleteGame(UUID gameId) {
        gameRepository.deleteById(gameId);
    }
    @Override
    public GameStatus endGame(UUID gameId) {
        GameEntity game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        return GameStatus.ONGOING;
    }

}
