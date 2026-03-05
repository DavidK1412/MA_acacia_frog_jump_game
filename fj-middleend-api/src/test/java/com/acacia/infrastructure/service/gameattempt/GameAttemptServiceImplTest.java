package com.acacia.infrastructure.service.gameattempt;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import com.acacia.infrastructure.persistence.entity.GameAttemptEntity;
import com.acacia.infrastructure.persistence.mapper.GameAttemptEntityMapper;
import com.acacia.infrastructure.persistence.repository.GameAttemptJpaRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("GameAttemptServiceImpl - Tests Unitarios")
class GameAttemptServiceImplTest {

    @Inject
    GameAttemptServiceImpl gameAttemptService;

    @Inject
    GameAttemptJpaRepository jpaRepository;

    @Inject
    GameAttemptEntityMapper mapper;

    @MockBean(GameAttemptJpaRepository.class)
    GameAttemptJpaRepository jpaRepository() {
        return mock(GameAttemptJpaRepository.class);
    }

    @MockBean(GameAttemptEntityMapper.class)
    GameAttemptEntityMapper mapper() {
        return mock(GameAttemptEntityMapper.class);
    }

    @Test
    @DisplayName("findById debe retornar un intento cuando existe")
    void shouldReturnAttemptWhenExists() {
        String attemptId = "attempt-123";
        GameAttemptEntity mockEntity = new GameAttemptEntity();
        mockEntity.setId(attemptId);

        GameAttempt mockAttempt = GameAttempt.builder()
                .id(attemptId)
                .isActive(true)
                .build();

        when(jpaRepository.findById(attemptId)).thenReturn(Optional.of(mockEntity));
        when(mapper.toDomain(mockEntity)).thenReturn(mockAttempt);

        Optional<GameAttempt> result = gameAttemptService.findById(attemptId);

        assertTrue(result.isPresent());
        assertEquals(attemptId, result.get().getId());
        
        verify(jpaRepository, times(1)).findById(attemptId);
        verify(mapper, times(1)).toDomain(mockEntity);
    }

    @Test
    @DisplayName("findById debe retornar Optional.empty cuando no existe")
    void shouldReturnEmptyWhenAttemptDoesNotExist() {
        String attemptId = "non-existent-attempt";

        when(jpaRepository.findById(attemptId)).thenReturn(Optional.empty());

        Optional<GameAttempt> result = gameAttemptService.findById(attemptId);

        assertFalse(result.isPresent());
        
        verify(jpaRepository, times(1)).findById(attemptId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findByGameId debe retornar todos los intentos de un juego")
    void shouldReturnAllAttemptsForGame() {
        String gameId = "game-123";
        GameAttemptEntity entity1 = new GameAttemptEntity();
        entity1.setId("attempt-1");
        GameAttemptEntity entity2 = new GameAttemptEntity();
        entity2.setId("attempt-2");

        GameAttempt attempt1 = GameAttempt.builder().id("attempt-1").build();
        GameAttempt attempt2 = GameAttempt.builder().id("attempt-2").build();

        when(jpaRepository.findByGameId(gameId)).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(attempt1);
        when(mapper.toDomain(entity2)).thenReturn(attempt2);

        List<GameAttempt> result = gameAttemptService.findByGameId(gameId);

        assertEquals(2, result.size());
        
        verify(jpaRepository, times(1)).findByGameId(gameId);
        verify(mapper, times(2)).toDomain(any(GameAttemptEntity.class));
    }

    @Test
    @DisplayName("findActiveByGameId debe retornar el intento activo")
    void shouldReturnActiveAttempt() {
        String gameId = "game-123";
        GameAttemptEntity mockEntity = new GameAttemptEntity();
        mockEntity.setId("active-attempt");
        mockEntity.setIsActive(true);

        GameAttempt mockAttempt = GameAttempt.builder()
                .id("active-attempt")
                .isActive(true)
                .build();

        when(jpaRepository.findByGameIdAndIsActive(gameId, true))
                .thenReturn(Optional.of(mockEntity));
        when(mapper.toDomain(mockEntity)).thenReturn(mockAttempt);

        Optional<GameAttempt> result = gameAttemptService.findActiveByGameId(gameId);

        assertTrue(result.isPresent());
        assertTrue(result.get().getIsActive());
        
        verify(jpaRepository, times(1)).findByGameIdAndIsActive(gameId, true);
    }

    @Test
    @DisplayName("findActiveByGameId debe retornar empty cuando no hay intento activo")
    void shouldReturnEmptyWhenNoActiveAttempt() {
        String gameId = "game-without-active";

        when(jpaRepository.findByGameIdAndIsActive(gameId, true))
                .thenReturn(Optional.empty());

        Optional<GameAttempt> result = gameAttemptService.findActiveByGameId(gameId);

        assertFalse(result.isPresent());
        
        verify(jpaRepository, times(1)).findByGameIdAndIsActive(gameId, true);
    }

    @Test
    @DisplayName("findAllActive debe retornar todos los intentos activos")
    void shouldReturnAllActiveAttempts() {
        GameAttemptEntity entity1 = new GameAttemptEntity();
        entity1.setId("attempt-1");
        entity1.setIsActive(true);

        GameAttempt attempt1 = GameAttempt.builder()
                .id("attempt-1")
                .isActive(true)
                .build();

        when(jpaRepository.findByIsActive(true)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(attempt1);

        List<GameAttempt> result = gameAttemptService.findAllActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        
        verify(jpaRepository, times(1)).findByIsActive(true);
    }

    @Test
    @DisplayName("findByDifficultyId debe retornar intentos por dificultad")
    void shouldReturnAttemptsByDifficulty() {
        Integer difficultyId = 2;
        GameAttemptEntity entity1 = new GameAttemptEntity();
        entity1.setId("attempt-1");

        GameAttempt attempt1 = GameAttempt.builder()
                .id("attempt-1")
                .build();

        when(jpaRepository.findByDifficultyId(difficultyId)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(attempt1);

        List<GameAttempt> result = gameAttemptService.findByDifficultyId(difficultyId);

        assertEquals(1, result.size());
        
        verify(jpaRepository, times(1)).findByDifficultyId(difficultyId);
    }

    @Test
    @DisplayName("save debe guardar y retornar el intento")
    void shouldSaveAndReturnAttempt() {
        GameAttempt attempt = GameAttempt.builder()
                .id("attempt-123")
                .isActive(true)
                .lastBuclicity(1.5f)
                .lastBranchFactor(2.0f)
                .build();

        GameAttemptEntity entity = new GameAttemptEntity();
        entity.setId("attempt-123");

        GameAttemptEntity savedEntity = new GameAttemptEntity();
        savedEntity.setId("attempt-123");

        GameAttempt savedAttempt = GameAttempt.builder()
                .id("attempt-123")
                .isActive(true)
                .build();

        when(mapper.toEntity(attempt)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedAttempt);

        GameAttempt result = gameAttemptService.save(attempt);

        assertNotNull(result);
        assertEquals("attempt-123", result.getId());
        
        verify(mapper, times(1)).toEntity(attempt);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(savedEntity);
    }

    @Test
    @DisplayName("deleteById debe invocar el repository correctamente")
    void shouldDeleteById() {
        String attemptId = "attempt-to-delete";

        doNothing().when(jpaRepository).deleteById(attemptId);

        gameAttemptService.deleteById(attemptId);

        verify(jpaRepository, times(1)).deleteById(attemptId);
    }

    @Test
    @DisplayName("findByGameId debe retornar lista vacía cuando no hay intentos")
    void shouldReturnEmptyListWhenNoAttempts() {
        String gameId = "game-without-attempts";

        when(jpaRepository.findByGameId(gameId)).thenReturn(List.of());

        List<GameAttempt> result = gameAttemptService.findByGameId(gameId);

        assertTrue(result.isEmpty());
        
        verify(jpaRepository, times(1)).findByGameId(gameId);
    }
}
