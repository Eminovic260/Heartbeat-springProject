package com.springbootproject.heartbeat.dao;

import com.springbootproject.heartbeat.model.Player;
import fr.le_campus_numerique.square_games.engine.Game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameDao {

    Game createGame(Game game);

    Optional<Game> getGame(UUID id);

    void updateGame(Game game);

    void deleteGame(UUID id);
    List<Game> getAllGames();
}
