package com.acacia.infrastructure.persistence.repository;

import com.acacia.infrastructure.persistence.entity.MovementMissEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Repository
public interface MovementMissJpaRepository extends JpaRepository<MovementMissEntity, String> {
    
    Optional<MovementMissEntity> findByGameAttemptId(String gameAttemptId);
    
    @Transactional
    void deleteByGameAttemptId(String gameAttemptId);
}
