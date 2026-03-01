package com.acacia.app.domain.services.external.game;

import com.acacia.app.domain.entity.Game;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<Game> findById(String id);
    List<Game> findAll();
    List<Game> findAllFinished();
    List<Game> findAllInProgress();
    Game save(Game game);
    void deleteById(String id);
    boolean existsById(String id);
}
