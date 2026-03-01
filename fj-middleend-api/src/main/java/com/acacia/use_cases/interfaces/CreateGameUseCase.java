package com.acacia.use_cases.interfaces;

import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;

public interface CreateGameUseCase {
    GameCreateOutput createGame(GameIntention movementInput);
}
