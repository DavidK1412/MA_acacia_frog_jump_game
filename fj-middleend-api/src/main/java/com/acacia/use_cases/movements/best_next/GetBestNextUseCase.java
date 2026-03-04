package com.acacia.use_cases.movements.best_next;

import com.acacia.app.domain.entity.game.decision.Decision;

public interface GetBestNextUseCase {
    Decision getBestNext(String gameId);
}
