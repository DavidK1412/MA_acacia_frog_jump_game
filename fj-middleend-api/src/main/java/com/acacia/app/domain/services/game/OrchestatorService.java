package com.acacia.app.domain.services.game;

import com.acacia.api.rest.exception.specific.NotFoundException;
import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.app.domain.services.external.game.GameService;
import com.acacia.app.domain.services.external.gameattempt.GameAttemptService;
import com.acacia.app.domain.services.external.graph.GraphService;
import com.acacia.app.domain.services.external.movement.MovementService;
import com.acacia.app.domain.services.external.speech.SpeechService;
import com.acacia.commons.state.StateHelper;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class OrchestatorService {
    private final GameService gameService;
    private final GraphService graphService;
    private final GameAttemptService gameAttemptService;
    private final MovementService movementService;
    private final SpeechService speechService;

    public Game createGame(GameIntention gameIntention) {
        Game game = Game.builder()
                .id(gameIntention.getGameId())
                .isFinished(false)
                .branchFactorAvg(0F)
                .buclicityAvg(0F)
                .build();

        return gameService.save(game);
    }

    public Decision getBestNext(String gameId) {
        Pair<Game, GameAttempt> gameAndAttempt = getGameAndActiveAttempt(gameId);
        Movement actualMovement = movementService.findLastMovementByAttemptId(gameAndAttempt.getRight().getId())
                .orElseThrow(() -> new NotFoundException("No movements found for active attempt of game with id " + gameId));

        NextMoveResponse response = graphService.getBestNextFromGraph(actualMovement.getFormatedState(), StateHelper.getTypeOfGoalState(gameAndAttempt.getRight().getDifficultyId()));

        if (response == null || response.nextMove() == null) {
            log.warn("No best next move found for game with id {}. Response from graph service: {}", gameId, response);
            return null;
        }

        Map<String, Object> actions = Map.of(
                Decision.DecisionType.BEST_NEXT.toLowerCase(), response.nextState(),
                "text",  speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response)
        );

        return Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(actions)
                .build();
    }

    private Pair<Game, GameAttempt> getGameAndActiveAttempt(String gameId) {
        Optional<Game> gameOpt = gameService.findById(gameId);
        if (gameOpt.isEmpty()) {
            throw new NotFoundException("Game with id " + gameId + " not found");
        }
        Game game = gameOpt.get();

        Optional<GameAttempt> activeAttemptOpt = gameAttemptService.findActiveByGameId(gameId);
        if (activeAttemptOpt.isEmpty()) {
            throw new NotFoundException("No active game attempt found for game with id " + gameId);
        }
        GameAttempt activeAttempt = activeAttemptOpt.get();

        return Pair.of(game, activeAttempt);
    }
}
