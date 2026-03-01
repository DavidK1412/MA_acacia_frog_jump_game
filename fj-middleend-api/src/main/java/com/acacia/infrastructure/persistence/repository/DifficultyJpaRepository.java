package com.acacia.infrastructure.persistence.repository;

import com.acacia.infrastructure.persistence.entity.DifficultyEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface DifficultyJpaRepository extends JpaRepository<DifficultyEntity, Integer> {
    
    Optional<DifficultyEntity> findByName(String name);
}
