package com.acacia.infrastructure.service.movementmiss;

import com.acacia.app.domain.entity.game.movement.MovementMiss;
import com.acacia.app.domain.services.external.movementmiss.MovementMissService;
import com.acacia.infrastructure.persistence.mapper.MovementMissEntityMapper;
import com.acacia.infrastructure.persistence.repository.MovementMissJpaRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class MovementMissServiceImpl implements MovementMissService {
    
    private final MovementMissJpaRepository jpaRepository;
    private final MovementMissEntityMapper mapper;
    
    @Override
    public Optional<MovementMiss> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<MovementMiss> findByGameAttemptId(String gameAttemptId) {
        return jpaRepository.findByGameAttemptId(gameAttemptId)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<MovementMiss> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public MovementMiss save(MovementMiss movementMiss) {
        var entity = mapper.toEntity(movementMiss);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public void deleteByGameAttemptId(String gameAttemptId) {
        jpaRepository.deleteByGameAttemptId(gameAttemptId);
    }
}
