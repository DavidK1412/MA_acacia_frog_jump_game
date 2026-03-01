package com.acacia.app.domain.services.external.gameattempt;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;

import java.util.List;
import java.util.Optional;

public interface GameAttemptService {
    Optional<GameAttempt> findById(String id);
    List<GameAttempt> findByGameId(String gameId);
    Optional<GameAttempt> findActiveByGameId(String gameId);
    List<GameAttempt> findAllActive();
    List<GameAttempt> findByDifficultyId(Integer difficultyId);
    GameAttempt save(GameAttempt gameAttempt);
    void deleteById(String id);
}
