package com.acacia.app.domain.services.external.difficulty;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;

import java.util.List;
import java.util.Optional;

public interface DifficultyService {
    List<Difficulty> findAll();
    Optional<Difficulty> findById(Integer id);
    Optional<Difficulty> findByName(String name);
    Difficulty save(Difficulty difficulty);
}
