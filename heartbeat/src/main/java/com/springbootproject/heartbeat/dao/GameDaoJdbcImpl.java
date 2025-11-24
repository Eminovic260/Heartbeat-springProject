package com.springbootproject.heartbeat.dao;

import com.springbootproject.heartbeat.model.Player;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GameDaoJdbcImpl implements GameDao {
    private final DataSource dataSource;

    public GameDaoJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Game createGame(Game game, List<Player> players) {

        String sql = "INSERT INTO game(uuid, board_size, game_type) VALUES(?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, game.getId().toString());
            stmt.setInt(2, game.getBoardSize());
            stmt.setString(3, game.getFactoryId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sqlPlayer = "INSERT INTO player(uuid, game_uuid, symbol) VALUES(?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlPlayer)) {
            for (Player p : players) {
                stmt.setString(1, p.getId().toString());
                stmt.setString(2, game.getId().toString());
                stmt.setString(3, p.getSymbol());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return game;
    }


    @Override
    public Optional<Game> getGame(UUID id) {
        String sqlGame = "SELECT * FROM game WHERE uuid = ?";
        String sqlPlayers = "SELECT uuid, symbol FROM player WHERE game_uuid = ?";
        String sqlTokens = "SELECT player_uuid, x, y FROM token WHERE game_uuid = ?";

        try (Connection connection = dataSource.getConnection()) {

            int boardSize;
            String gameType;

            try (PreparedStatement stmt = connection.prepareStatement(sqlGame)) {
                stmt.setString(1, id.toString());
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return Optional.empty();
                }

                boardSize = rs.getInt("board_size");
                gameType = rs.getString("game_type");
            }

            GameFactory factory;
            switch (gameType) {
                case "tictactoe" -> factory = new TicTacToeGameFactory();
                case "connectfour" -> factory = new ConnectFourGameFactory();
                default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
            }

            List<UUID> playerIds = new ArrayList<>();
            List<String> playerSymbols = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sqlPlayers)) {
                stmt.setString(1, id.toString());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("uuid"));
                    String symbol = rs.getString("symbol");
                    playerIds.add(playerId);
                    playerSymbols.add(symbol);
                }
            }

            if (playerIds.size() != 2) {
                throw new IllegalStateException("A game must have exactly 2 players");
            }

            List<TokenPosition<UUID>> boardTokens = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sqlTokens)) {
                stmt.setString(1, id.toString());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("player_uuid"));
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");

                    int index = playerIds.indexOf(playerId);
                    String symbol = (index != -1) ? playerSymbols.get(index) : "?";

                    boardTokens.add(new TokenPosition<>(playerId, symbol, x, y));
                }
            }

            Game game = factory.createGameWithIds(
                id,
                boardSize,
                playerIds,
                boardTokens,
                List.of()
            );

            return Optional.of(game);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void updateGame(Game game) {
        String sql = "UPDATE game SET board_size = ?, game_type = ? WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, game.getBoardSize());
            stmt.setString(2, game.getFactoryId());
            stmt.setString(3, game.getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteGame(UUID id) {
        String sql = "DELETE FROM game WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Game> getAllGames() {
        String sql = "SELECT uuid FROM game";
        List<Game> games = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("uuid"));
                getGame(id).ifPresent(games::add);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return games;
    }


    public void addToken(UUID gameId, UUID playerId, int x, int y) {
        String sql = "INSERT INTO token(uuid, game_uuid, player_uuid, x, y) VALUES(?,?,?,?,?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, gameId.toString());
            stmt.setString(3, playerId.toString());
            stmt.setInt(4, x);
            stmt.setInt(5, y);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
