package com.acacia.infrastructure.client.dto.graph;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record NextMove(
        String type,
        Integer frogId,
        Integer fromIndex,
        Integer toIndex
) {
}
