package com.acacia.use_cases;

import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import com.acacia.app.domain.services.game.OrchestatorService;
import com.acacia.use_cases.commons.Messages;
import com.acacia.use_cases.exceptions.gameUseCase.CreateGameException;
import com.acacia.use_cases.interfaces.CreateGameUseCase;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class CreateGameUseCaseImpl implements CreateGameUseCase {

    private final OrchestatorService orchestatorService;

    @Override
    public GameCreateOutput createGame(GameIntention gameIntention) {
        try {
            Game game = orchestatorService.createGame(gameIntention);
            return GameCreateOutput.builder()
                    .gameId(game.getId())
                    .message(Messages.GAME_CREATED.getMessage())
                    .build();
        } catch (Exception ex) {
            log.error("Error al crear el juego: {}", ex.getMessage(), ex);
            throw new CreateGameException("No se pudo crear el juego. Por favor, inténtalo de nuevo.");
        }
    }
}
