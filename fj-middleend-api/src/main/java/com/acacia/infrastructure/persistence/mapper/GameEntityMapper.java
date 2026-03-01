package com.acacia.infrastructure.persistence.mapper;

import com.acacia.app.domain.entity.Game;
import com.acacia.infrastructure.persistence.entity.GameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper de MapStruct para convertir entre Game (dominio) y GameEntity (infraestructura).
 * Este mapper mantiene la separación entre las capas de dominio e infraestructura.
 */
@Mapper(componentModel = "jsr330")
public interface GameEntityMapper {
    
    GameEntityMapper INSTANCE = Mappers.getMapper(GameEntityMapper.class);
    
    /**
     * Convierte una entidad de dominio a entidad JPA.
     * @param game entidad de dominio
     * @return entidad JPA
     */
    GameEntity toEntity(Game game);
    
    /**
     * Convierte una entidad JPA a entidad de dominio.
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    Game toDomain(GameEntity entity);
}
