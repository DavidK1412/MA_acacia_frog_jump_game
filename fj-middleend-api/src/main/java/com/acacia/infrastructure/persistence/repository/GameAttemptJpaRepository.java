package com.acacia.infrastructure.persistence.repository;

import com.acacia.infrastructure.persistence.entity.GameAttemptEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameAttemptJpaRepository extends JpaRepository<GameAttemptEntity, String> {
    
    List<GameAttemptEntity> findByGameId(String gameId);
    
    Optional<GameAttemptEntity> findByGameIdAndIsActive(String gameId, Boolean isActive);
    
    List<GameAttemptEntity> findByIsActive(Boolean isActive);
    
    List<GameAttemptEntity> findByDifficultyId(Integer difficultyId);
}
