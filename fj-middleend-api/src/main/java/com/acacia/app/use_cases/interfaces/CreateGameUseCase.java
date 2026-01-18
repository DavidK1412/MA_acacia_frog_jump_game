package com.acacia.app.use_cases.interfaces;

import com.acacia.app.domain.entity.game.create.CreateInput;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;

public interface CreateGameUseCase {
    GameCreateOutput createGame(CreateInput movementInput);
}
