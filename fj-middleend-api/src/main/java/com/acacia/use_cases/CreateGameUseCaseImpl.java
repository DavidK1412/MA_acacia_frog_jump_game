package com.acacia.use_cases;

import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import com.acacia.use_cases.interfaces.CreateGameUseCase;
import jakarta.inject.Singleton;

@Singleton
public class CreateGameUseCaseImpl implements CreateGameUseCase {
    @Override
    public GameCreateOutput createGame(GameIntention movementInput) {
        return GameCreateOutput.builder()
                .gameId("test-game-id")
                .message("Game created successfully")
                .build();
    }
}
