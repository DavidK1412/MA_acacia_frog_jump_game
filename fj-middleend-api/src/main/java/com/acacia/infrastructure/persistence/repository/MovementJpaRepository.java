package com.acacia.infrastructure.persistence.repository;

import com.acacia.infrastructure.persistence.entity.MovementEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovementJpaRepository extends JpaRepository<MovementEntity, String> {
    
    List<MovementEntity> findByAttemptId(String attemptId);
    
    List<MovementEntity> findByAttemptIdOrderByStepAsc(String attemptId);
    
    List<MovementEntity> findByAttemptIdAndIsCorrect(String attemptId, Boolean isCorrect);
    
    @Query("SELECT m FROM MovementEntity m WHERE m.attemptId = :attemptId ORDER BY m.step DESC")
    Optional<MovementEntity> findLastMovementByAttemptId(String attemptId);
    
    long countByAttemptId(String attemptId);
}
