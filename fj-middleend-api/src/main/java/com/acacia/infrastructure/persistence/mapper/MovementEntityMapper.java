package com.acacia.infrastructure.persistence.mapper;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.infrastructure.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper de MapStruct para convertir entre Movement (dominio) y MovementEntity (infraestructura).
 * Este mapper mantiene la separación entre las capas de dominio e infraestructura.
 * 
 * Maneja la conversión de composición de objetos (dominio) a IDs (infraestructura).
 */
@Mapper(componentModel = "jsr330")
public interface MovementEntityMapper {
    
    MovementEntityMapper INSTANCE = Mappers.getMapper(MovementEntityMapper.class);
    
    /**
     * Convierte una entidad de dominio a entidad JPA.
     * Extrae el ID del objeto compuesto.
     * 
     * @param movement entidad de dominio
     * @return entidad JPA
     */
    @Mapping(target = "attemptId", source = "attempt.id")
    @Mapping(target = "gameAttempt", ignore = true)
    MovementEntity toEntity(Movement movement);
    
    /**
     * Convierte una entidad JPA a entidad de dominio.
     * Construye el objeto compuesto a partir del ID y entidad relacionada.
     * 
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    @Mapping(target = "attempt", expression = "java(buildGameAttempt(entity))")
    Movement toDomain(MovementEntity entity);
    
    /**
     * Construye el objeto GameAttempt a partir de la entidad JPA.
     * Si la relación está cargada (eager), usa la entidad completa.
     * Si no, crea un objeto GameAttempt solo con el ID.
     */
    default GameAttempt buildGameAttempt(MovementEntity entity) {
        if (entity.getGameAttempt() != null) {
            // Relación cargada, convertir entidad completa
            return GameAttemptEntityMapper.INSTANCE.toDomain(entity.getGameAttempt());
        } else if (entity.getAttemptId() != null) {
            // Solo tenemos el ID, crear objeto parcial
            return GameAttempt.builder()
                    .id(entity.getAttemptId())
                    .build();
        }
        return null;
    }
}
