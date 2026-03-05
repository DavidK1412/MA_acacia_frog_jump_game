package com.acacia.infrastructure.service.graph;

import com.acacia.infrastructure.client.dto.graph.NextMove;
import com.acacia.infrastructure.client.dto.graph.NextMoveOptions;
import com.acacia.infrastructure.client.dto.graph.NextMoveRequest;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import com.acacia.infrastructure.client.graph.GraphApiClient;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("GraphServiceImpl - Tests Unitarios")
class GraphServiceImplTest {

    @Inject
    GraphServiceImpl graphService;

    @Inject
    GraphApiClient graphApiClient;

    @MockBean(GraphApiClient.class)
    GraphApiClient graphApiClient() {
        return mock(GraphApiClient.class);
    }

    @Test
    @DisplayName("getBestNextFromGraph debe retornar respuesta exitosa cuando la API responde correctamente")
    void shouldReturnSuccessfulResponseWhenApiRespondsCorrectly() {
        List<Integer> state = List.of(1, 2, 0, 3, 4, 5, 6);
        String goal = "default";
        
        NextMove mockNextMove = new NextMove("STEP", 3, 2, 3);
        NextMoveResponse mockResponse = new NextMoveResponse(
                true,
                3,
                mockNextMove,
                List.of(1, 2, 3, 0, 4, 5, 6),
                null,
                null
        );

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenReturn(mockResponse);

        NextMoveResponse result = graphService.getBestNextFromGraph(state, goal);

        assertNotNull(result);
        assertTrue(result.found());
        assertEquals(3, result.level());
        assertNotNull(result.nextMove());
        assertEquals("STEP", result.nextMove().type());
        assertEquals(3, result.nextMove().frogId());
        
        verify(graphApiClient, times(1)).getNextMove(any(NextMoveRequest.class));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe construir correctamente el NextMoveRequest")
    void shouldConstructNextMoveRequestCorrectly() {
        List<Integer> state = List.of(1, 2, 0, 3, 4, 5, 6);
        String goal = "default";
        
        NextMoveResponse mockResponse = new NextMoveResponse(
                true, 3, null, null, null, null
        );

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenReturn(mockResponse);

        graphService.getBestNextFromGraph(state, goal);

        verify(graphApiClient).getNextMove(argThat(request -> 
                request.state().equals(state) &&
                request.goal().equals("default") &&
                request.options().returnMeta() &&
                request.options().returnNextState()
        ));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe retornar respuesta con found=false cuando no hay solución")
    void shouldReturnNotFoundResponseWhenNoSolutionExists() {
        List<Integer> state = List.of(1, 2, 3, 4, 5, 6, 0);
        String goal = "default";
        
        NextMoveResponse mockResponse = new NextMoveResponse(
                false,
                3,
                null,
                null,
                null,
                "NOT_REACHABLE"
        );

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenReturn(mockResponse);

        NextMoveResponse result = graphService.getBestNextFromGraph(state, goal);

        assertNotNull(result);
        assertFalse(result.found());
        assertEquals("NOT_REACHABLE", result.reason());
        assertNull(result.nextMove());
        
        verify(graphApiClient, times(1)).getNextMove(any(NextMoveRequest.class));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe lanzar GraphApiException cuando la API retorna error HTTP")
    void shouldThrowGraphApiExceptionWhenApiReturnsHttpError() {
        List<Integer> state = List.of(1, 2, 0, 3, 4, 5, 6);
        String goal = "default";

        HttpClientResponseException httpException = mock(HttpClientResponseException.class);
        when(httpException.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(httpException.getMessage()).thenReturn("Invalid state");

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenThrow(httpException);

        GraphApiException exception = assertThrows(
                GraphApiException.class,
                () -> graphService.getBestNextFromGraph(state, goal)
        );

        assertTrue(exception.getMessage().contains("Failed to get next move from graph API"));
        assertNotNull(exception.getCause());
        
        verify(graphApiClient, times(1)).getNextMove(any(NextMoveRequest.class));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe lanzar GraphApiException cuando hay error de conexión")
    void shouldThrowGraphApiExceptionWhenConnectionFails() {
        List<Integer> state = List.of(1, 2, 0, 3, 4, 5, 6);
        String goal = "default";

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        GraphApiException exception = assertThrows(
                GraphApiException.class,
                () -> graphService.getBestNextFromGraph(state, goal)
        );

        assertTrue(exception.getMessage().contains("Unexpected error communicating with graph API"));
        assertNotNull(exception.getCause());
        
        verify(graphApiClient, times(1)).getNextMove(any(NextMoveRequest.class));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe manejar diferentes niveles de dificultad")
    void shouldHandleDifferentDifficultyLevels() {
        List<Integer> easyState = List.of(1, 2, 0, 3, 4, 5, 6);
        List<Integer> mediumState = List.of(1, 2, 0, 3, 4, 5, 6, 7, 8);
        List<Integer> hardState = List.of(1, 2, 0, 3, 4, 5, 6, 7, 8, 9, 10);

        NextMoveResponse easyResponse = new NextMoveResponse(true, 1, null, null, null, null);
        NextMoveResponse mediumResponse = new NextMoveResponse(true, 2, null, null, null, null);
        NextMoveResponse hardResponse = new NextMoveResponse(true, 3, null, null, null, null);

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenReturn(easyResponse)
                .thenReturn(mediumResponse)
                .thenReturn(hardResponse);

        NextMoveResponse result1 = graphService.getBestNextFromGraph(easyState, "default");
        NextMoveResponse result2 = graphService.getBestNextFromGraph(mediumState, "default");
        NextMoveResponse result3 = graphService.getBestNextFromGraph(hardState, "default");

        assertEquals(1, result1.level());
        assertEquals(2, result2.level());
        assertEquals(3, result3.level());
        
        verify(graphApiClient, times(3)).getNextMove(any(NextMoveRequest.class));
    }

    @Test
    @DisplayName("getBestNextFromGraph debe manejar respuestas con metadata completa")
    void shouldHandleResponsesWithCompleteMetadata() {
        List<Integer> state = List.of(1, 2, 0, 3, 4, 5, 6);
        String goal = "default";
        
        NextMove mockNextMove = new NextMove("STEP", 3, 2, 3);
        NextMoveResponse mockResponse = new NextMoveResponse(
                true,
                3,
                mockNextMove,
                List.of(1, 2, 3, 0, 4, 5, 6),
                null,
                null
        );

        when(graphApiClient.getNextMove(any(NextMoveRequest.class)))
                .thenReturn(mockResponse);

        NextMoveResponse result = graphService.getBestNextFromGraph(state, goal);

        assertNotNull(result);
        assertNotNull(result.nextState());
        assertEquals(7, result.nextState().size());
        
        verify(graphApiClient, times(1)).getNextMove(any(NextMoveRequest.class));
    }
}
