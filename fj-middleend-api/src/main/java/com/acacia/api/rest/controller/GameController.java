package com.acacia.api.rest.controller;

import com.acacia.api.rest.dto.*;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;

public interface GameController {
    HttpResponse<GameCreateResponse> createGame(@Body GameCreateRequest request);
    HttpResponse<MovementResponse> movement(@PathVariable String gameId, @Body MovementRequest request);
    HttpResponse<MovementResponse> getBestNextMove(@PathVariable String gameId);
    HttpResponse<DefaultMessageResponse> registerMistake(@PathVariable String gameId);
}
