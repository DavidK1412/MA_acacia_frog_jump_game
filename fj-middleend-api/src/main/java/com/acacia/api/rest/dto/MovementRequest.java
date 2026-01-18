package com.acacia.api.rest.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record MovementRequest(
        List<Integer> movement
) {
}
