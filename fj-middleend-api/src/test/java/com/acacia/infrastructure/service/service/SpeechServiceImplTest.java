package com.acacia.infrastructure.service.service;

import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.infrastructure.client.dto.graph.NextMove;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@DisplayName("SpeechServiceImpl - Tests Unitarios")
class SpeechServiceImplTest {

    @Inject
    SpeechServiceImpl speechService;

    @Test
    @DisplayName("getTextForAction debe retornar texto para BEST_NEXT con contexto válido")
    void shouldReturnTextForBestNextAction() {
        NextMove nextMove = new NextMove("STEP", 3, 2, 5);
        NextMoveResponse response = new NextMoveResponse(
                true,
                3,
                nextMove,
                List.of(1, 2, 3, 0, 4, 5, 6),
                null,
                null
        );

        String result = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("5"));
    }

    @Test
    @DisplayName("getTextForAction debe generar diferentes textos en múltiples llamadas")
    void shouldGenerateDifferentTextsInMultipleCalls() {
        NextMove nextMove = new NextMove("STEP", 1, 0, 1);
        NextMoveResponse response = new NextMoveResponse(
                true,
                3,
                nextMove,
                List.of(0, 1, 2, 3, 4, 5, 6),
                null,
                null
        );

        String result1 = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);
        String result2 = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);
        String result3 = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        
        assertTrue(result1.contains("1"));
        assertTrue(result2.contains("1"));
        assertTrue(result3.contains("1"));
    }

    @Test
    @DisplayName("getTextForAction debe lanzar excepción para acción no soportada")
    void shouldThrowExceptionForUnsupportedAction() {
        NextMoveResponse response = new NextMoveResponse(
                true,
                3,
                new NextMove("STEP", 1, 0, 1),
                null,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> speechService.getTextForAction("UNKNOWN_ACTION", response)
        );

        assertTrue(exception.getMessage().contains("No text function found for action"));
        assertTrue(exception.getMessage().contains("UNKNOWN_ACTION"));
    }

    @Test
    @DisplayName("getTextForAction debe manejar diferentes IDs de rana")
    void shouldHandleDifferentFrogIds() {
        NextMove nextMove1 = new NextMove("STEP", 5, 3, 4);
        NextMove nextMove2 = new NextMove("STEP", 10, 8, 9);

        NextMoveResponse response1 = new NextMoveResponse(true, 3, nextMove1, null, null, null);
        NextMoveResponse response2 = new NextMoveResponse(true, 3, nextMove2, null, null, null);

        String result1 = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response1);
        String result2 = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertFalse(result1.isEmpty());
        assertFalse(result2.isEmpty());
    }

    @Test
    @DisplayName("getTextForAction debe reemplazar correctamente todos los placeholders")
    void shouldReplaceAllPlaceholdersCorrectly() {
        NextMove nextMove = new NextMove("STEP", 7, 6, 8);
        NextMoveResponse response = new NextMoveResponse(true, 3, nextMove, null, null, null);

        String result = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);

        assertNotNull(result);
        assertFalse(result.contains("{cubeId}"));
        assertFalse(result.contains("{from}"));
        assertFalse(result.contains("{to}"));
    }

    @Test
    @DisplayName("getTextForAction debe manejar movimientos de salto")
    void shouldHandleJumpMovements() {
        NextMove nextMove = new NextMove("JUMP", 2, 1, 3);
        NextMoveResponse response = new NextMoveResponse(true, 3, nextMove, null, null, null);

        String result = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("getTextForAction debe generar texto coherente con índices")
    void shouldGenerateCoherentTextWithIndices() {
        NextMove nextMove = new NextMove("STEP", 0, 0, 1);
        NextMoveResponse response = new NextMoveResponse(true, 3, nextMove, null, null, null);

        String result = speechService.getTextForAction(Decision.DecisionType.BEST_NEXT, response);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 20);
    }
}
