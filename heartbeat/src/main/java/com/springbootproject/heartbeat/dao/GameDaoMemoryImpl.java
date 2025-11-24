/*package com.springbootproject.heartbeat.dao;

import fr.le_campus_numerique.square_games.engine.Game;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class GameDaoMemoryImpl implements GameDao {
    private final Map<UUID, Game> games = new HashMap<>();

    @Override
    public Game createGame(Game game) {
        games.put(game.getId(),game);
        return game;
    }

    @Override
    public Optional<Game> getGame(UUID id){
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public void updateGame(Game game){
        games.put(game.getId(),game);
    }

    @Override
    public void deleteGame(UUID id){
        games.remove(id);
    }
@Override
    public List<Game> getAllGames(){
        return new ArrayList<>(games.values());
}
}
*/
