package com.acacia.use_cases;

import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import com.acacia.app.domain.services.game.OrchestatorService;
import com.acacia.use_cases.exceptions.gameUseCase.CreateGameException;
import com.acacia.use_cases.game.interfaces.CreateGameUseCase;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("CreateGameUseCase Tests")
class CreateGameUseCaseTest {

    @Inject
    CreateGameUseCase createGameUseCase;

    @Inject
    OrchestatorService orchestatorService;

    @MockBean(OrchestatorService.class)
    OrchestatorService orchestatorService() {
        return mock(OrchestatorService.class);
    }

    @Test
    @DisplayName("Debe crear un juego exitosamente")
    void shouldCreateGameSuccessfully() {
        GameIntention intention = GameIntention.builder()
                .gameId("test-game-id")
                .build();

        Game mockGame = Game.builder()
                .id("game-123")
                .isFinished(false)
                .buclicityAvg(0.0f)
                .branchFactorAvg(0.0f)
                .build();

        when(orchestatorService.createGame(any(GameIntention.class)))
                .thenReturn(mockGame);

        GameCreateOutput result = createGameUseCase.createGame(intention);

        assertNotNull(result);
        assertEquals("game-123", result.getGameId());
        assertNotNull(result.getMessage());
        
        verify(orchestatorService, times(1)).createGame(intention);
    }

    @Test
    @DisplayName("Debe lanzar CreateGameException cuando el servicio falla")
    void shouldThrowCreateGameExceptionWhenServiceFails() {
        GameIntention intention = GameIntention.builder()
                .gameId("test-game-id")
                .build();

        when(orchestatorService.createGame(any(GameIntention.class)))
                .thenThrow(new RuntimeException("Database error"));

        CreateGameException exception = assertThrows(
                CreateGameException.class,
                () -> createGameUseCase.createGame(intention)
        );

        assertEquals(422, exception.getStatusCode());
        assertEquals("CREATE_GAME_FAILED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("No se pudo crear el juego"));
        
        verify(orchestatorService, times(1)).createGame(intention);
    }

    @Test
    @DisplayName("Debe lanzar CreateGameException cuando el orchestrator retorna null")
    void shouldThrowExceptionWhenOrchestratorReturnsNull() {
        GameIntention intention = GameIntention.builder()
                .gameId("test-game-id")
                .build();

        when(orchestatorService.createGame(any(GameIntention.class)))
                .thenReturn(null);

        CreateGameException exception = assertThrows(
                CreateGameException.class,
                () -> createGameUseCase.createGame(intention)
        );

        assertNotNull(exception);
        verify(orchestatorService, times(1)).createGame(intention);
    }

    @Test
    @DisplayName("Debe crear juego con diferentes intenciones")
    void shouldCreateGameWithDifferentIntentions() {
        GameIntention intention1 = GameIntention.builder()
                .gameId("game-1")
                .build();

        GameIntention intention2 = GameIntention.builder()
                .gameId("game-2")
                .build();

        Game mockGame1 = Game.builder()
                .id("game-1")
                .isFinished(false)
                .build();

        Game mockGame2 = Game.builder()
                .id("game-2")
                .isFinished(false)
                .build();

        when(orchestatorService.createGame(intention1)).thenReturn(mockGame1);
        when(orchestatorService.createGame(intention2)).thenReturn(mockGame2);

        GameCreateOutput result1 = createGameUseCase.createGame(intention1);
        GameCreateOutput result2 = createGameUseCase.createGame(intention2);

        assertEquals("game-1", result1.getGameId());
        assertEquals("game-2", result2.getGameId());
        
        verify(orchestatorService, times(1)).createGame(intention1);
        verify(orchestatorService, times(1)).createGame(intention2);
    }

    @Test
    @DisplayName("Debe manejar IllegalArgumentException del orchestrator")
    void shouldHandleIllegalArgumentException() {
        GameIntention intention = GameIntention.builder()
                .gameId("invalid-id")
                .build();

        when(orchestatorService.createGame(any(GameIntention.class)))
                .thenThrow(new IllegalArgumentException("Invalid game ID"));

        CreateGameException exception = assertThrows(
                CreateGameException.class,
                () -> createGameUseCase.createGame(intention)
        );

        assertNotNull(exception.getMessage());
        verify(orchestatorService, times(1)).createGame(intention);
    }
}
