package com.acacia.infrastructure.service.gameattempt;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.app.domain.services.external.gameattempt.GameAttemptService;
import com.acacia.infrastructure.persistence.mapper.GameAttemptEntityMapper;
import com.acacia.infrastructure.persistence.repository.GameAttemptJpaRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class GameAttemptServiceImpl implements GameAttemptService {
    
    private final GameAttemptJpaRepository jpaRepository;
    private final GameAttemptEntityMapper mapper;
    
    @Override
    public Optional<GameAttempt> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<GameAttempt> findByGameId(String gameId) {
        return jpaRepository.findByGameId(gameId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<GameAttempt> findActiveByGameId(String gameId) {
        return jpaRepository.findByGameIdAndIsActive(gameId, true)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<GameAttempt> findAllActive() {
        return jpaRepository.findByIsActive(true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GameAttempt> findByDifficultyId(Integer difficultyId) {
        return jpaRepository.findByDifficultyId(difficultyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public GameAttempt save(GameAttempt gameAttempt) {
        var entity = mapper.toEntity(gameAttempt);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
