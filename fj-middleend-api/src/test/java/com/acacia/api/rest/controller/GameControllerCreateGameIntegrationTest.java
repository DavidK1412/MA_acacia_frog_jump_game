package com.acacia.api.rest.controller;

import com.acacia.api.rest.dto.GameCreateRequest;
import com.acacia.api.rest.dto.GameCreateResponse;
import com.acacia.api.rest.routes.GameControllerRoutes;
import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.create.GameIntention;
import com.acacia.app.domain.entity.game.output.GameCreateOutput;
import com.acacia.app.domain.services.game.OrchestatorService;
import com.acacia.use_cases.exceptions.gameUseCase.CreateGameException;
import com.acacia.use_cases.game.interfaces.CreateGameUseCase;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("GameController - Tests de Integración (createGame)")
class GameControllerCreateGameIntegrationTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    CreateGameUseCase createGameUseCase;

    @MockBean(CreateGameUseCase.class)
    CreateGameUseCase createGameUseCase() {
        return mock(CreateGameUseCase.class);
    }

    @Test
    @DisplayName("POST /games debe crear un juego exitosamente y retornar 201")
    void shouldCreateGameSuccessfullyAndReturn201() {
        GameCreateRequest request = new GameCreateRequest("game-123");
        
        GameCreateOutput mockOutput = GameCreateOutput.builder()
                .gameId("game-123")
                .message("Juego creado exitosamente")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput);

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request);
        
        HttpResponse<GameCreateResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, GameCreateResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        GameCreateResponse body = response.getBody().get();
        assertEquals("game-123", body.gameId());
        assertEquals("Juego creado exitosamente", body.message());
        
        verify(createGameUseCase, times(1)).createGame(any(GameIntention.class));
    }

    @Test
    @DisplayName("POST /games debe retornar 422 cuando el use case lanza CreateGameException")
    void shouldReturn422WhenUseCaseThrowsCreateGameException() {
        GameCreateRequest request = new GameCreateRequest("invalid-game");

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenThrow(new CreateGameException("No se pudo crear el juego"));

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request);

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(httpRequest, GameCreateResponse.class)
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        
        verify(createGameUseCase, times(1)).createGame(any(GameIntention.class));
    }

    @Test
    @DisplayName("POST /games debe validar el request body")
    void shouldValidateRequestBody() {
        HttpRequest<String> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, "");

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(httpRequest, GameCreateResponse.class)
        );

        assertTrue(
                exception.getStatus() == HttpStatus.BAD_REQUEST ||
                exception.getStatus() == HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }

    @Test
    @DisplayName("POST /games debe mapear correctamente el gameId del request al use case")
    void shouldMapGameIdCorrectlyFromRequestToUseCase() {
        String expectedGameId = "specific-game-xyz";
        GameCreateRequest request = new GameCreateRequest(expectedGameId);

        GameCreateOutput mockOutput = GameCreateOutput.builder()
                .gameId(expectedGameId)
                .message("Juego creado")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput);

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request);

        HttpResponse<GameCreateResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, GameCreateResponse.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(expectedGameId, response.getBody().get().gameId());
        
        verify(createGameUseCase).createGame(argThat(intention -> 
                intention.getGameId().equals(expectedGameId)
        ));
    }

    @Test
    @DisplayName("POST /games debe retornar 201 CREATED con body válido")
    void shouldReturn201CreatedWithValidBody() {
        GameCreateRequest request = new GameCreateRequest("game-123");

        GameCreateOutput mockOutput = GameCreateOutput.builder()
                .gameId("game-123")
                .message("Juego creado exitosamente")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput);

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request);

        HttpResponse<GameCreateResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, GameCreateResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertNotNull(response.getBody().get().gameId());
    }

    @Test
    @DisplayName("POST /games debe aceptar Content-Type application/json")
    void shouldAcceptApplicationJsonContentType() {
        GameCreateRequest request = new GameCreateRequest("game-123");

        GameCreateOutput mockOutput = GameCreateOutput.builder()
                .gameId("game-123")
                .message("Juego creado")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput);

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request)
                .contentType("application/json");

        HttpResponse<GameCreateResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, GameCreateResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    @DisplayName("POST /games debe retornar response con estructura correcta")
    void shouldReturnResponseWithCorrectStructure() {
        GameCreateRequest request = new GameCreateRequest("game-123");

        GameCreateOutput mockOutput = GameCreateOutput.builder()
                .gameId("game-123")
                .message("Juego creado exitosamente")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput);

        HttpRequest<GameCreateRequest> httpRequest = HttpRequest
                .POST(GameControllerRoutes.BASE, request);

        HttpResponse<GameCreateResponse> response = httpClient.toBlocking()
                .exchange(httpRequest, GameCreateResponse.class);

        assertTrue(response.getBody().isPresent());
        GameCreateResponse body = response.getBody().get();
        
        assertNotNull(body.gameId());
        assertNotNull(body.message());
        assertFalse(body.gameId().isEmpty());
        assertFalse(body.message().isEmpty());
    }

    @Test
    @DisplayName("POST /games debe manejar múltiples creaciones de juegos consecutivas")
    void shouldHandleMultipleConsecutiveGameCreations() {
        GameCreateRequest request1 = new GameCreateRequest("game-1");
        GameCreateRequest request2 = new GameCreateRequest("game-2");

        GameCreateOutput mockOutput1 = GameCreateOutput.builder()
                .gameId("game-1")
                .message("Juego 1 creado")
                .build();

        GameCreateOutput mockOutput2 = GameCreateOutput.builder()
                .gameId("game-2")
                .message("Juego 2 creado")
                .build();

        when(createGameUseCase.createGame(any(GameIntention.class)))
                .thenReturn(mockOutput1)
                .thenReturn(mockOutput2);

        HttpResponse<GameCreateResponse> response1 = httpClient.toBlocking()
                .exchange(HttpRequest.POST(GameControllerRoutes.BASE, request1), GameCreateResponse.class);

        HttpResponse<GameCreateResponse> response2 = httpClient.toBlocking()
                .exchange(HttpRequest.POST(GameControllerRoutes.BASE, request2), GameCreateResponse.class);

        assertEquals(HttpStatus.CREATED, response1.getStatus());
        assertEquals(HttpStatus.CREATED, response2.getStatus());
        
        assertEquals("game-1", response1.getBody().get().gameId());
        assertEquals("game-2", response2.getBody().get().gameId());
        
        verify(createGameUseCase, times(2)).createGame(any(GameIntention.class));
    }
}
