package com.acacia.api.rest.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;

@Serdeable
public record MovementResponse(
        String type,
        Map<String, Object> actions
) {
}
