package com.acacia.infrastructure.persistence.repository;

import com.acacia.infrastructure.persistence.entity.GameEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, String> {
    
    List<GameEntity> findByIsFinished(Boolean isFinished);
}
