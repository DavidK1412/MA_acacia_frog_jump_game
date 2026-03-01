package com.acacia.infrastructure.persistence.mapper;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.infrastructure.persistence.entity.DifficultyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "jsr330")
public interface DifficultyEntityMapper {
    
    DifficultyEntityMapper INSTANCE = Mappers.getMapper(DifficultyEntityMapper.class);
    
    DifficultyEntity toEntity(Difficulty difficulty);
    
    Difficulty toDomain(DifficultyEntity entity);
}
