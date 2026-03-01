package com.acacia.infrastructure.service.game;

import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.services.external.game.GameService;
import com.acacia.infrastructure.persistence.mapper.GameEntityMapper;
import com.acacia.infrastructure.persistence.repository.GameJpaRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    
    private final GameJpaRepository jpaRepository;
    private final GameEntityMapper mapper;
    
    @Override
    public Optional<Game> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Game> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Game> findAllFinished() {
        return jpaRepository.findByIsFinished(true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Game> findAllInProgress() {
        return jpaRepository.findByIsFinished(false).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Game save(Game game) {
        var entity = mapper.toEntity(game);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }
}
