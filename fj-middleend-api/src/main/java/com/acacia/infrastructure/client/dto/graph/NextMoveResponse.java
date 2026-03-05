package com.acacia.infrastructure.client.dto.graph;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record NextMoveResponse(
        Boolean found,
        Integer level,
        NextMove nextMove,
        List<Integer> nextState,
        NextMoveMeta meta,
        String reason
) {
}
