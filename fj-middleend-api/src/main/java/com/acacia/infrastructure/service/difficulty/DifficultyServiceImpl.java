package com.acacia.infrastructure.service.difficulty;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.app.domain.services.external.difficulty.DifficultyService;
import com.acacia.infrastructure.persistence.mapper.DifficultyEntityMapper;
import com.acacia.infrastructure.persistence.repository.DifficultyJpaRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class DifficultyServiceImpl implements DifficultyService {
    
    private final DifficultyJpaRepository jpaRepository;
    private final DifficultyEntityMapper mapper;
    
    @Override
    public List<Difficulty> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Difficulty> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Difficulty> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }
    
    @Override
    public Difficulty save(Difficulty difficulty) {
        var entity = mapper.toEntity(difficulty);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
