package com.acacia.infrastructure.service.game;

import com.acacia.app.domain.entity.Game;
import com.acacia.infrastructure.persistence.entity.GameEntity;
import com.acacia.infrastructure.persistence.mapper.GameEntityMapper;
import com.acacia.infrastructure.persistence.repository.GameJpaRepository;
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
@DisplayName("GameServiceImpl - Tests Unitarios")
class GameServiceImplTest {

    @Inject
    GameServiceImpl gameService;

    @Inject
    GameJpaRepository jpaRepository;

    @Inject
    GameEntityMapper mapper;

    @MockBean(GameJpaRepository.class)
    GameJpaRepository jpaRepository() {
        return mock(GameJpaRepository.class);
    }

    @MockBean(GameEntityMapper.class)
    GameEntityMapper mapper() {
        return mock(GameEntityMapper.class);
    }

    @Test
    @DisplayName("findById debe retornar un juego cuando existe")
    void shouldReturnGameWhenExists() {
        String gameId = "game-123";
        GameEntity mockEntity = new GameEntity();
        mockEntity.setId(gameId);
        mockEntity.setIsFinished(false);

        Game mockGame = Game.builder()
                .id(gameId)
                .isFinished(false)
                .build();

        when(jpaRepository.findById(gameId)).thenReturn(Optional.of(mockEntity));
        when(mapper.toDomain(mockEntity)).thenReturn(mockGame);

        Optional<Game> result = gameService.findById(gameId);

        assertTrue(result.isPresent());
        assertEquals(gameId, result.get().getId());
        assertFalse(result.get().getIsFinished());
        
        verify(jpaRepository, times(1)).findById(gameId);
        verify(mapper, times(1)).toDomain(mockEntity);
    }

    @Test
    @DisplayName("findById debe retornar Optional.empty cuando no existe")
    void shouldReturnEmptyWhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(jpaRepository.findById(gameId)).thenReturn(Optional.empty());

        Optional<Game> result = gameService.findById(gameId);

        assertFalse(result.isPresent());
        
        verify(jpaRepository, times(1)).findById(gameId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findAll debe retornar lista de todos los juegos")
    void shouldReturnAllGames() {
        GameEntity entity1 = new GameEntity();
        entity1.setId("game-1");
        GameEntity entity2 = new GameEntity();
        entity2.setId("game-2");

        Game game1 = Game.builder().id("game-1").build();
        Game game2 = Game.builder().id("game-2").build();

        when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(game1);
        when(mapper.toDomain(entity2)).thenReturn(game2);

        List<Game> result = gameService.findAll();

        assertEquals(2, result.size());
        assertEquals("game-1", result.get(0).getId());
        assertEquals("game-2", result.get(1).getId());
        
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(2)).toDomain(any(GameEntity.class));
    }

    @Test
    @DisplayName("findAll debe retornar lista vacía cuando no hay juegos")
    void shouldReturnEmptyListWhenNoGames() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<Game> result = gameService.findAll();

        assertTrue(result.isEmpty());
        
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findAllFinished debe retornar solo juegos finalizados")
    void shouldReturnOnlyFinishedGames() {
        GameEntity entity1 = new GameEntity();
        entity1.setId("game-1");
        entity1.setIsFinished(true);

        Game game1 = Game.builder().id("game-1").isFinished(true).build();

        when(jpaRepository.findByIsFinished(true)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(game1);

        List<Game> result = gameService.findAllFinished();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsFinished());
        
        verify(jpaRepository, times(1)).findByIsFinished(true);
    }

    @Test
    @DisplayName("findAllInProgress debe retornar solo juegos en progreso")
    void shouldReturnOnlyInProgressGames() {
        GameEntity entity1 = new GameEntity();
        entity1.setId("game-1");
        entity1.setIsFinished(false);

        Game game1 = Game.builder().id("game-1").isFinished(false).build();

        when(jpaRepository.findByIsFinished(false)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(game1);

        List<Game> result = gameService.findAllInProgress();

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsFinished());
        
        verify(jpaRepository, times(1)).findByIsFinished(false);
    }

    @Test
    @DisplayName("save debe guardar y retornar el juego")
    void shouldSaveAndReturnGame() {
        Game game = Game.builder()
                .id("game-123")
                .isFinished(false)
                .buclicityAvg(0.5f)
                .branchFactorAvg(1.2f)
                .build();

        GameEntity entity = new GameEntity();
        entity.setId("game-123");

        GameEntity savedEntity = new GameEntity();
        savedEntity.setId("game-123");

        Game savedGame = Game.builder()
                .id("game-123")
                .isFinished(false)
                .buclicityAvg(0.5f)
                .branchFactorAvg(1.2f)
                .build();

        when(mapper.toEntity(game)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedGame);

        Game result = gameService.save(game);

        assertNotNull(result);
        assertEquals("game-123", result.getId());
        
        verify(mapper, times(1)).toEntity(game);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(savedEntity);
    }

    @Test
    @DisplayName("deleteById debe invocar el repository correctamente")
    void shouldDeleteById() {
        String gameId = "game-to-delete";

        doNothing().when(jpaRepository).deleteById(gameId);

        gameService.deleteById(gameId);

        verify(jpaRepository, times(1)).deleteById(gameId);
    }

    @Test
    @DisplayName("existsById debe retornar true cuando el juego existe")
    void shouldReturnTrueWhenGameExists() {
        String gameId = "existing-game";

        when(jpaRepository.existsById(gameId)).thenReturn(true);

        boolean result = gameService.existsById(gameId);

        assertTrue(result);
        
        verify(jpaRepository, times(1)).existsById(gameId);
    }

    @Test
    @DisplayName("existsById debe retornar false cuando el juego no existe")
    void shouldReturnFalseWhenGameDoesNotExist() {
        String gameId = "non-existent-game";

        when(jpaRepository.existsById(gameId)).thenReturn(false);

        boolean result = gameService.existsById(gameId);

        assertFalse(result);
        
        verify(jpaRepository, times(1)).existsById(gameId);
    }

    @Test
    @DisplayName("save debe manejar correctamente juegos con promedios")
    void shouldHandleGameWithAverages() {
        Game game = Game.builder()
                .id("game-with-stats")
                .isFinished(true)
                .buclicityAvg(2.5f)
                .branchFactorAvg(3.8f)
                .build();

        GameEntity entity = new GameEntity();
        GameEntity savedEntity = new GameEntity();
        Game savedGame = Game.builder()
                .id("game-with-stats")
                .buclicityAvg(2.5f)
                .branchFactorAvg(3.8f)
                .build();

        when(mapper.toEntity(game)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedGame);

        Game result = gameService.save(game);

        assertNotNull(result);
        assertEquals(2.5f, result.getBuclicityAvg());
        assertEquals(3.8f, result.getBranchFactorAvg());
    }
}
