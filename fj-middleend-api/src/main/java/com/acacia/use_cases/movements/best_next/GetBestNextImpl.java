package com.acacia.use_cases.movements.best_next;

import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.app.domain.services.game.OrchestatorService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class GetBestNextImpl implements GetBestNextUseCase {

    private final OrchestatorService orchestatorService;

    @Override
    public Decision getBestNext(String gameId) {
        return orchestatorService.getBestNext(gameId);
    }
}
