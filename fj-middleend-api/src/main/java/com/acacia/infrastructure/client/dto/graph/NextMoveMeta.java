package com.acacia.infrastructure.client.dto.graph;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record NextMoveMeta(
        List<Integer> goalState,
        Integer predictedRemainingCost,
        Integer legalMovesFromState,
        String strategy,
        String policyVersion,
        String loadSource,
        Integer timeMs
) {
}
