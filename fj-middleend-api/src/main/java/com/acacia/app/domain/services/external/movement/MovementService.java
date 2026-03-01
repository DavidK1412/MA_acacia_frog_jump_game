package com.acacia.app.domain.services.external.movement;

import com.acacia.app.domain.entity.game.movement.Movement;

import java.util.List;
import java.util.Optional;

public interface MovementService {
    Optional<Movement> findById(String id);
    List<Movement> findByAttemptId(String attemptId);
    List<Movement> findByAttemptIdOrderByStep(String attemptId);
    List<Movement> findCorrectMovementsByAttemptId(String attemptId);
    List<Movement> findIncorrectMovementsByAttemptId(String attemptId);
    Optional<Movement> findLastMovementByAttemptId(String attemptId);
    Movement save(Movement movement);
    void deleteById(String id);
    long countByAttemptId(String attemptId);
}
