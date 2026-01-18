package com.acacia.api.rest.controller;

import com.acacia.api.rest.dto.DefaultMessageResponse;
import com.acacia.api.rest.dto.GameCreateRequest;
import com.acacia.api.rest.dto.GameCreateResponse;
import com.acacia.api.rest.dto.MovementRequest;
import com.acacia.api.rest.dto.MovementResponse;
import com.acacia.api.rest.mappers.GameEntityMapper;
import com.acacia.api.rest.routes.GameControllerRoutes;
import com.acacia.app.domain.entity.game.create.CreateInput;
import com.acacia.app.use_cases.interfaces.CreateGameUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import lombok.RequiredArgsConstructor;

@Controller(GameControllerRoutes.BASE)
@RequiredArgsConstructor
public class GameControllerImpl implements GameController {

    private final CreateGameUseCase createGameUseCase;

    @Override
    @Post
    public HttpResponse<GameCreateResponse> createGame(@Body GameCreateRequest request) {
        CreateInput input = GameEntityMapper.INSTANCE.toCreateInput(request);
        GameCreateResponse response = GameEntityMapper.INSTANCE.fromCreateOutput(createGameUseCase.createGame(input));

        return HttpResponse.created(response);
    }

    @Override
    @Post(GameControllerRoutes.REGISTER_MOVEMENT)
    public HttpResponse<MovementResponse> movement(@PathVariable String gameId, @Body MovementRequest request) {
        return null;
    }

    @Override
    @Get(GameControllerRoutes.BEST_NEXT_MOVE)
    public HttpResponse<MovementResponse> getBestNextMove(@PathVariable String gameId) {
        return null;
    }

    @Override
    @Post(GameControllerRoutes.REGISTER_MISS)
    public HttpResponse<DefaultMessageResponse> registerMistake(@PathVariable String gameId) {
        return null;
    }
}
