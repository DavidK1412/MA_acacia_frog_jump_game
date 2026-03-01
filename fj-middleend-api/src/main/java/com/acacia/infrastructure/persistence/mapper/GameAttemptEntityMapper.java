package com.acacia.infrastructure.persistence.mapper;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.infrastructure.persistence.entity.GameAttemptEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper de MapStruct para convertir entre GameAttempt (dominio) y GameAttemptEntity (infraestructura).
 * Este mapper mantiene la separación entre las capas de dominio e infraestructura.
 * 
 * Maneja la conversión de composición de objetos (dominio) a IDs (infraestructura).
 */
@Mapper(componentModel = "jsr330")
public interface GameAttemptEntityMapper {
    
    GameAttemptEntityMapper INSTANCE = Mappers.getMapper(GameAttemptEntityMapper.class);
    
    /**
     * Convierte una entidad de dominio a entidad JPA.
     * Extrae los IDs de los objetos compuestos.
     * 
     * @param gameAttempt entidad de dominio
     * @return entidad JPA
     */
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "difficultyId", source = "difficulty.id")
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "difficulty", ignore = true)
    GameAttemptEntity toEntity(GameAttempt gameAttempt);
    
    /**
     * Convierte una entidad JPA a entidad de dominio.
     * Construye los objetos compuestos a partir de los IDs y entidades relacionadas.
     * 
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    @Mapping(target = "game", expression = "java(buildGame(entity))")
    @Mapping(target = "difficulty", expression = "java(buildDifficulty(entity))")
    GameAttempt toDomain(GameAttemptEntity entity);
    
    /**
     * Construye el objeto Game a partir de la entidad JPA.
     * Si la relación está cargada (eager), usa la entidad completa.
     * Si no, crea un objeto Game solo con el ID.
     */
    default Game buildGame(GameAttemptEntity entity) {
        if (entity.getGame() != null) {
            // Relación cargada, convertir entidad completa
            return GameEntityMapper.INSTANCE.toDomain(entity.getGame());
        } else if (entity.getGameId() != null) {
            // Solo tenemos el ID, crear objeto parcial
            return Game.builder()
                    .id(entity.getGameId())
                    .build();
        }
        return null;
    }
    
    /**
     * Construye el objeto Difficulty a partir de la entidad JPA.
     * Si la relación está cargada (eager), usa la entidad completa.
     * Si no, crea un objeto Difficulty solo con el ID.
     */
    default Difficulty buildDifficulty(GameAttemptEntity entity) {
        if (entity.getDifficulty() != null) {
            // Relación cargada, convertir entidad completa
            return DifficultyEntityMapper.INSTANCE.toDomain(entity.getDifficulty());
        } else if (entity.getDifficultyId() != null) {
            // Solo tenemos el ID, crear objeto parcial
            return Difficulty.builder()
                    .id(entity.getDifficultyId())
                    .build();
        }
        return null;
    }
}
