package com.acacia.api.rest.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GameCreateResponse(
        String message,
        String gameId
) {
}
