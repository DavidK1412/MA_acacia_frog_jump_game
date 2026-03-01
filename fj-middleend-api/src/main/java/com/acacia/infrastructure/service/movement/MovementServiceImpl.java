package com.acacia.infrastructure.service.movement;

import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.app.domain.services.external.movement.MovementService;
import com.acacia.infrastructure.persistence.mapper.MovementEntityMapper;
import com.acacia.infrastructure.persistence.repository.MovementJpaRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    
    private final MovementJpaRepository jpaRepository;
    private final MovementEntityMapper mapper;
    
    @Override
    public Optional<Movement> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Movement> findByAttemptId(String attemptId) {
        return jpaRepository.findByAttemptId(attemptId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Movement> findByAttemptIdOrderByStep(String attemptId) {
        return jpaRepository.findByAttemptIdOrderByStepAsc(attemptId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Movement> findCorrectMovementsByAttemptId(String attemptId) {
        return jpaRepository.findByAttemptIdAndIsCorrect(attemptId, true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Movement> findIncorrectMovementsByAttemptId(String attemptId) {
        return jpaRepository.findByAttemptIdAndIsCorrect(attemptId, false).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Movement> findLastMovementByAttemptId(String attemptId) {
        return jpaRepository.findLastMovementByAttemptId(attemptId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Movement save(Movement movement) {
        var entity = mapper.toEntity(movement);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public long countByAttemptId(String attemptId) {
        return jpaRepository.countByAttemptId(attemptId);
    }
}
