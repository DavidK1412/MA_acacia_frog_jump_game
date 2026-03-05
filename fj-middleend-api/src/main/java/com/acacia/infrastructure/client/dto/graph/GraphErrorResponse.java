package com.acacia.infrastructure.client.dto.graph;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

@Introspected
@Serdeable
public record GraphErrorResponse(
        String error,
        String message,
        Map<String, Object> details
) {
}
