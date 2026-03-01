package com.acacia.app.domain.services.game;

import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.services.external.game.GameService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class OrchestatorService {
    private final GameService gameService;

    public Game createGame(GameIntention gameIntention) {
        Game game = Game.builder()
                .id(gameIntention.getGameId())
                .isFinished(false)
                .branchFactorAvg(0F)
                .buclicityAvg(0F)
                .build();

        return gameService.save(game);
    }
}
