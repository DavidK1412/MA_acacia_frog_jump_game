package com.acacia.api.rest.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record DefaultMessageResponse(
        String message
) {
}
