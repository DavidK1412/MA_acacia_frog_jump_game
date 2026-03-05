package com.acacia.api.rest.controller;

import com.acacia.api.rest.dto.MovementResponse;
import com.acacia.api.rest.routes.GameControllerRoutes;
import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.use_cases.movements.best_next.GetBestNextUseCase;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.acacia.api.rest.exception.specific.NotFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("GameController - Tests de Integración (getBestNextMove)")
class GameControllerGetBestNextMoveIntegrationTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    GetBestNextUseCase getBestNextUseCase;

    @MockBean(GetBestNextUseCase.class)
    GetBestNextUseCase getBestNextUseCase() {
        return mock(GetBestNextUseCase.class);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe retornar el mejor movimiento exitosamente")
    void shouldReturnBestNextMoveSuccessfully() {
        String gameId = "game-123";
        
        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of(
                        "best_next", List.of(1, 2, 3, 0, 4, 5, 6),
                        "text", "Mueve el cubo 3 de 2 a 3"
                ))
                .build();

        when(getBestNextUseCase.getBestNext(gameId))
                .thenReturn(mockDecision);

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpResponse<MovementResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, MovementResponse.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        MovementResponse body = response.getBody().get();
        assertEquals(Decision.DecisionType.BEST_NEXT, body.type());
        assertNotNull(body.actions());
        assertTrue(body.actions().containsKey("best_next"));
        assertTrue(body.actions().containsKey("text"));
        
        verify(getBestNextUseCase, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe retornar 404 cuando el juego no existe")
    void shouldReturn404WhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(getBestNextUseCase.getBestNext(gameId))
                .thenThrow(new NotFoundException("Game with id " + gameId + " not found"));

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(httpRequest, MovementResponse.class)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        
        verify(getBestNextUseCase, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe retornar 404 cuando no hay intento activo")
    void shouldReturn404WhenNoActiveAttempt() {
        String gameId = "game-without-active-attempt";

        when(getBestNextUseCase.getBestNext(gameId))
                .thenThrow(new NotFoundException("No active game attempt found for game with id " + gameId));

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(httpRequest, MovementResponse.class)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        
        verify(getBestNextUseCase, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe retornar 404 cuando no hay movimientos")
    void shouldReturn404WhenNoMovements() {
        String gameId = "game-without-movements";

        when(getBestNextUseCase.getBestNext(gameId))
                .thenThrow(new NotFoundException("No movements found for active attempt of game with id " + gameId));

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(httpRequest, MovementResponse.class)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        
        verify(getBestNextUseCase, times(1)).getBestNext(gameId);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe extraer correctamente el gameId de la ruta")
    void shouldExtractGameIdCorrectlyFromPath() {
        String gameId = "specific-game-uuid-xyz";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(getBestNextUseCase.getBestNext(gameId))
                .thenReturn(mockDecision);

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        httpClient.toBlocking().exchange(httpRequest, MovementResponse.class);

        verify(getBestNextUseCase).getBestNext(gameId);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe retornar Content-Type application/json")
    void shouldReturnApplicationJsonContentType() {
        String gameId = "game-123";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(getBestNextUseCase.getBestNext(gameId))
                .thenReturn(mockDecision);

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpResponse<MovementResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, MovementResponse.class);

        assertTrue(response.getContentType().isPresent());
        assertTrue(response.getContentType().get().toString().contains("application/json"));
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe incluir acciones en la respuesta")
    void shouldIncludeActionsInResponse() {
        String gameId = "game-123";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of(
                        "best_next", List.of(1, 2, 3, 0, 4, 5, 6),
                        "text", "Texto de ayuda",
                        "from", 2,
                        "to", 3
                ))
                .build();

        when(getBestNextUseCase.getBestNext(gameId))
                .thenReturn(mockDecision);

        String url = GameControllerRoutes.BASE + "/" + gameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpResponse<MovementResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, MovementResponse.class);

        assertTrue(response.getBody().isPresent());
        MovementResponse body = response.getBody().get();
        
        assertNotNull(body.actions());
        assertTrue(body.actions().size() >= 2);
    }

    @Test
    @DisplayName("GET /game/{gameId}/best_next debe manejar gameIds con diferentes formatos")
    void shouldHandleGameIdsWithDifferentFormats() {
        String uuidGameId = "550e8400-e29b-41d4-a716-446655440000";

        Decision mockDecision = Decision.builder()
                .type(Decision.DecisionType.BEST_NEXT)
                .actions(Map.of("best_next", List.of(1, 2, 3, 0, 4, 5, 6)))
                .build();

        when(getBestNextUseCase.getBestNext(uuidGameId))
                .thenReturn(mockDecision);

        String url = GameControllerRoutes.BASE + "/" + uuidGameId + "/best_next";
        HttpRequest<Object> httpRequest = HttpRequest.GET(url);

        HttpResponse<MovementResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, MovementResponse.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        
        verify(getBestNextUseCase, times(1)).getBestNext(uuidGameId);
    }
}
