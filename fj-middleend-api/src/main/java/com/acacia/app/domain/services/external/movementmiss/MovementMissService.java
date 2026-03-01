package com.acacia.app.domain.services.external.movementmiss;

import com.acacia.app.domain.entity.game.movement.MovementMiss;

import java.util.List;
import java.util.Optional;

public interface MovementMissService {
    Optional<MovementMiss> findById(String id);
    Optional<MovementMiss> findByGameAttemptId(String gameAttemptId);
    List<MovementMiss> findAll();
    MovementMiss save(MovementMiss movementMiss);
    void deleteById(String id);
    void deleteByGameAttemptId(String gameAttemptId);
}
