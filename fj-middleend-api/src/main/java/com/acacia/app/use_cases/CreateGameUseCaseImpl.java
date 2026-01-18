package com.acacia.app.use_cases;

import com.acacia.app.domain.entity.game.create.CreateInput;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import com.acacia.app.use_cases.interfaces.CreateGameUseCase;
import jakarta.inject.Singleton;

@Singleton
public class CreateGameUseCaseImpl implements CreateGameUseCase {
    @Override
    public GameCreateOutput createGame(CreateInput movementInput) {
        return GameCreateOutput.builder()
                .gameId("test-game-id")
                .message("Game created successfully")
                .build();
    }
}
