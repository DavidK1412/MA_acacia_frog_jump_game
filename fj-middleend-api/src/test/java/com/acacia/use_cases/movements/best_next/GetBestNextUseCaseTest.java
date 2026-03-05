package com.acacia.use_cases.movements.best_next;

import com.acacia.api.rest.exception.specific.NotFoundException;
import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.app.domain.services.game.OrchestatorService;
import com.acacia.infrastructure.client.dto.graph.NextMove;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("GetBestNextUseCase - Tests Funcionales")
class GetBestNextUseCaseTest {

    @Inject
    GetBestNextUseCase getBestNextUseCase;

    @Inject
    OrchestatorService orchestatorService;

    @MockBean(OrchestatorService.class)
    OrchestatorService orchestatorService() {
        return mock(OrchestatorService.class);
    }

    @Test
    @DisplayName("Debe obtener el mejor siguiente movimiento exitosamente")
    void shouldGetBestNextMoveSuccessfully() {
        String gameId = "game-123";
        
        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of(
                        "best_next", List.of(1, 2, 3, 0, 4, 5, 6),
                        "text", "Mueve el cubo 3 de 2 a 3"
                ))
                .build();

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(mockDecision);

        Decision result = getBestNextUseCase.getBestNext(gameId);

        assertNotNull(result);
        assertEquals(Decision.DecisionType.BEST_NEXT, result.getType());
        assertNotNull(result.getActions());
        assertTrue(result.getActions().containsKey("best_next"));
        assertTrue(result.getActions().containsKey("text"));
        
        verify(orchestatorService, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe propagar NotFoundException cuando el juego no existe")
    void shouldPropagateNotFoundExceptionWhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(orchestatorService.getBestNext(gameId))
                .thenThrow(new NotFoundException("Game with id " + gameId + " not found"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getBestNextUseCase.getBestNext(gameId)
        );

        assertTrue(exception.getMessage().contains(gameId));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(orchestatorService, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe propagar NotFoundException cuando no hay intento activo")
    void shouldPropagateNotFoundExceptionWhenNoActiveAttempt() {
        String gameId = "game-without-active-attempt";

        when(orchestatorService.getBestNext(gameId))
                .thenThrow(new NotFoundException("No active game attempt found for game with id " + gameId));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getBestNextUseCase.getBestNext(gameId)
        );

        assertTrue(exception.getMessage().contains("No active game attempt"));
        
        verify(orchestatorService, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe propagar NotFoundException cuando no hay movimientos")
    void shouldPropagateNotFoundExceptionWhenNoMovements() {
        String gameId = "game-without-movements";

        when(orchestatorService.getBestNext(gameId))
                .thenThrow(new NotFoundException("No movements found for active attempt of game with id " + gameId));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getBestNextUseCase.getBestNext(gameId)
        );

        assertTrue(exception.getMessage().contains("No movements found"));
        
        verify(orchestatorService, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe manejar respuesta null del orchestrator")
    void shouldHandleNullResponseFromOrchestrator() {
        String gameId = "game-123";

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(null);

        Decision result = getBestNextUseCase.getBestNext(gameId);

        assertNull(result);
        
        verify(orchestatorService, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe obtener decisión con diferentes estados del juego")
    void shouldGetDecisionWithDifferentGameStates() {
        String gameId1 = "game-easy";
        String gameId2 = "game-medium";

        Decision easyDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(4, 5, 6, 0, 1, 2, 3)))
                .build();

        Decision mediumDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(5, 6, 7, 8, 0, 1, 2, 3, 4)))
                .build();

        when(orchestatorService.getBestNext(gameId1)).thenReturn(easyDecision);
        when(orchestatorService.getBestNext(gameId2)).thenReturn(mediumDecision);

        Decision result1 = getBestNextUseCase.getBestNext(gameId1);
        Decision result2 = getBestNextUseCase.getBestNext(gameId2);

        assertNotNull(result1);
        assertNotNull(result2);
        
        List<?> state1 = (List<?>) result1.getActions().get("best_next");
        List<?> state2 = (List<?>) result2.getActions().get("best_next");
        
        assertEquals(7, state1.size());
        assertEquals(9, state2.size());
    }

    @Test
    @DisplayName("Debe obtener decisión con texto de ayuda incluido")
    void shouldGetDecisionWithHelpText() {
        String gameId = "game-123";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of(
                        "best_next", List.of(1, 2, 3, 0, 4, 5, 6),
                        "text", "Mueve el cubo 3 de la posición 2 a la 3. Ese cubo ya quería pasear."
                ))
                .build();

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(mockDecision);

        Decision result = getBestNextUseCase.getBestNext(gameId);

        assertNotNull(result);
        assertTrue(result.getActions().containsKey("text"));
        String text = (String) result.getActions().get("text");
        assertNotNull(text);
        assertFalse(text.isEmpty());
    }

    @Test
    @DisplayName("Debe invocar el orchestrator exactamente una vez por llamada")
    void shouldInvokeOrchestratorExactlyOncePerCall() {
        String gameId = "game-123";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(mockDecision);

        getBestNextUseCase.getBestNext(gameId);
        getBestNextUseCase.getBestNext(gameId);

        verify(orchestatorService, times(2)).getBestNext(gameId);
    }

    @Test
    @DisplayName("Debe manejar correctamente el tipo de decisión BEST_NEXT")
    void shouldHandleBestNextDecisionTypeCorrectly() {
        String gameId = "game-123";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(mockDecision);

        Decision result = getBestNextUseCase.getBestNext(gameId);

        assertEquals(Decision.DecisionType.BEST_NEXT, result.getType());
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas consecutivas con el mismo gameId")
    void shouldHandleMultipleConsecutiveCallsWithSameGameId() {
        String gameId = "game-123";

        Decision decision1 = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 0, 3, 4, 5, 6)))
                .build();

        Decision decision2 = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(orchestatorService.getBestNext(gameId))
                .thenReturn(decision1)
                .thenReturn(decision2);

        Decision result1 = getBestNextUseCase.getBestNext(gameId);
        Decision result2 = getBestNextUseCase.getBestNext(gameId);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getActions().get("best_next"), result2.getActions().get("best_next"));
    }
}
