package com.acacia.api.rest.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record GameCreateResponse(
        String message,
        String gameId
) {
}
