package com.acacia.api.rest.mappers;

import com.acacia.api.rest.dto.MovementResponse;
import com.acacia.app.domain.entity.game.decision.Decision;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MovementDecisionEntityMapper {
    MovementDecisionEntityMapper INSTANCE = Mappers.getMapper(MovementDecisionEntityMapper.class);

    MovementResponse fromDecision(Decision decision);
}
