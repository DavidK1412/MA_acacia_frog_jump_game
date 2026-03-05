package com.acacia.infrastructure.persistence.mapper;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.infrastructure.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "jsr330")
public interface MovementEntityMapper {
    
    MovementEntityMapper INSTANCE = Mappers.getMapper(MovementEntityMapper.class);

    @Mapping(target = "attemptId", source = "attempt.id")
    @Mapping(target = "gameAttempt", ignore = true)
    MovementEntity toEntity(Movement movement);

    @Mapping(target = "attempt", expression = "java(buildGameAttempt(entity))")
    @Mapping(target = "formatedState", expression = "java(mapFormatedState(entity.getMovement()))")
    Movement toDomain(MovementEntity entity);

    default List<Integer> mapFormatedState(String state) {
        if (state == null || state.isEmpty()) {
            return List.of();
        }

        String cleanedState = state.replaceAll("\\s+", "");
        return Arrays.stream(cleanedState.split(","))
                .map(Integer::valueOf)
                .toList();
    }

    default GameAttempt buildGameAttempt(MovementEntity entity) {
        if (entity.getGameAttempt() != null) {
            return GameAttemptEntityMapper.INSTANCE.toDomain(entity.getGameAttempt());
        } else if (entity.getAttemptId() != null) {
            return GameAttempt.builder()
                    .id(entity.getAttemptId())
                    .build();
        }
        return null;
    }
}
