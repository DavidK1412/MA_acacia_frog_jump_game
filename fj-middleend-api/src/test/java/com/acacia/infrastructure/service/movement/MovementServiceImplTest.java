package com.acacia.infrastructure.service.movement;

import com.acacia.app.domain.entity.game.movement.Movement;
import com.acacia.infrastructure.persistence.entity.MovementEntity;
import com.acacia.infrastructure.persistence.mapper.MovementEntityMapper;
import com.acacia.infrastructure.persistence.repository.MovementJpaRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@DisplayName("MovementServiceImpl - Tests Unitarios")
class MovementServiceImplTest {

    @Inject
    MovementServiceImpl movementService;

    @Inject
    MovementJpaRepository jpaRepository;

    @Inject
    MovementEntityMapper mapper;

    @MockBean(MovementJpaRepository.class)
    MovementJpaRepository jpaRepository() {
        return mock(MovementJpaRepository.class);
    }

    @MockBean(MovementEntityMapper.class)
    MovementEntityMapper mapper() {
        return mock(MovementEntityMapper.class);
    }

    @Test
    @DisplayName("findById debe retornar un movimiento cuando existe")
    void shouldReturnMovementWhenExists() {
        String movementId = "movement-123";
        MovementEntity mockEntity = new MovementEntity();
        mockEntity.setId(movementId);

        Movement mockMovement = Movement.builder()
                .id(movementId)
                .step(1)
                .build();

        when(jpaRepository.findById(movementId)).thenReturn(Optional.of(mockEntity));
        when(mapper.toDomain(mockEntity)).thenReturn(mockMovement);

        Optional<Movement> result = movementService.findById(movementId);

        assertTrue(result.isPresent());
        assertEquals(movementId, result.get().getId());
        
        verify(jpaRepository, times(1)).findById(movementId);
        verify(mapper, times(1)).toDomain(mockEntity);
    }

    @Test
    @DisplayName("findById debe retornar Optional.empty cuando no existe")
    void shouldReturnEmptyWhenMovementDoesNotExist() {
        String movementId = "non-existent-movement";

        when(jpaRepository.findById(movementId)).thenReturn(Optional.empty());

        Optional<Movement> result = movementService.findById(movementId);

        assertFalse(result.isPresent());
        
        verify(jpaRepository, times(1)).findById(movementId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findByAttemptId debe retornar todos los movimientos de un intento")
    void shouldReturnAllMovementsForAttempt() {
        String attemptId = "attempt-123";
        MovementEntity entity1 = new MovementEntity();
        entity1.setId("movement-1");
        MovementEntity entity2 = new MovementEntity();
        entity2.setId("movement-2");

        Movement movement1 = Movement.builder().id("movement-1").build();
        Movement movement2 = Movement.builder().id("movement-2").build();

        when(jpaRepository.findByAttemptId(attemptId)).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(movement1);
        when(mapper.toDomain(entity2)).thenReturn(movement2);

        List<Movement> result = movementService.findByAttemptId(attemptId);

        assertEquals(2, result.size());
        
        verify(jpaRepository, times(1)).findByAttemptId(attemptId);
        verify(mapper, times(2)).toDomain(any(MovementEntity.class));
    }

    @Test
    @DisplayName("findByAttemptIdOrderByStep debe retornar movimientos ordenados")
    void shouldReturnMovementsOrderedByStep() {
        String attemptId = "attempt-123";
        MovementEntity entity1 = new MovementEntity();
        entity1.setId("movement-1");
        entity1.setStep(1);

        MovementEntity entity2 = new MovementEntity();
        entity2.setId("movement-2");
        entity2.setStep(2);

        Movement movement1 = Movement.builder().id("movement-1").step(1).build();
        Movement movement2 = Movement.builder().id("movement-2").step(2).build();

        when(jpaRepository.findByAttemptIdOrderByStepAsc(attemptId))
                .thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(movement1);
        when(mapper.toDomain(entity2)).thenReturn(movement2);

        List<Movement> result = movementService.findByAttemptIdOrderByStep(attemptId);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getStep());
        assertEquals(2, result.get(1).getStep());
        
        verify(jpaRepository, times(1)).findByAttemptIdOrderByStepAsc(attemptId);
    }

    @Test
    @DisplayName("findCorrectMovementsByAttemptId debe retornar solo movimientos correctos")
    void shouldReturnOnlyCorrectMovements() {
        String attemptId = "attempt-123";
        MovementEntity entity1 = new MovementEntity();
        entity1.setId("movement-1");
        entity1.setIsCorrect(true);

        Movement movement1 = Movement.builder()
                .id("movement-1")
                .isCorrect(true)
                .build();

        when(jpaRepository.findByAttemptIdAndIsCorrect(attemptId, true))
                .thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(movement1);

        List<Movement> result = movementService.findCorrectMovementsByAttemptId(attemptId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsCorrect());
        
        verify(jpaRepository, times(1)).findByAttemptIdAndIsCorrect(attemptId, true);
    }

    @Test
    @DisplayName("findIncorrectMovementsByAttemptId debe retornar solo movimientos incorrectos")
    void shouldReturnOnlyIncorrectMovements() {
        String attemptId = "attempt-123";
        MovementEntity entity1 = new MovementEntity();
        entity1.setId("movement-1");
        entity1.setIsCorrect(false);

        Movement movement1 = Movement.builder()
                .id("movement-1")
                .isCorrect(false)
                .build();

        when(jpaRepository.findByAttemptIdAndIsCorrect(attemptId, false))
                .thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(movement1);

        List<Movement> result = movementService.findIncorrectMovementsByAttemptId(attemptId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsCorrect());
        
        verify(jpaRepository, times(1)).findByAttemptIdAndIsCorrect(attemptId, false);
    }

    @Test
    @DisplayName("findLastMovementByAttemptId debe retornar el último movimiento")
    void shouldReturnLastMovement() {
        String attemptId = "attempt-123";
        MovementEntity mockEntity = new MovementEntity();
        mockEntity.setId("last-movement");
        mockEntity.setStep(10);

        Movement mockMovement = Movement.builder()
                .id("last-movement")
                .step(10)
                .build();

        when(jpaRepository.findLastMovementByAttemptId(attemptId))
                .thenReturn(Optional.of(mockEntity));
        when(mapper.toDomain(mockEntity)).thenReturn(mockMovement);

        Optional<Movement> result = movementService.findLastMovementByAttemptId(attemptId);

        assertTrue(result.isPresent());
        assertEquals("last-movement", result.get().getId());
        assertEquals(10, result.get().getStep());
        
        verify(jpaRepository, times(1)).findLastMovementByAttemptId(attemptId);
    }

    @Test
    @DisplayName("findLastMovementByAttemptId debe retornar empty cuando no hay movimientos")
    void shouldReturnEmptyWhenNoLastMovement() {
        String attemptId = "attempt-without-movements";

        when(jpaRepository.findLastMovementByAttemptId(attemptId))
                .thenReturn(Optional.empty());

        Optional<Movement> result = movementService.findLastMovementByAttemptId(attemptId);

        assertFalse(result.isPresent());
        
        verify(jpaRepository, times(1)).findLastMovementByAttemptId(attemptId);
    }

    @Test
    @DisplayName("save debe guardar y retornar el movimiento")
    void shouldSaveAndReturnMovement() {
        Movement movement = Movement.builder()
                .id("movement-123")
                .step(5)
                .movement("STEP")
                .isCorrect(true)
                .interruption(false)
                .formatedState(List.of(1, 2, 0, 3, 4, 5, 6))
                .build();

        MovementEntity entity = new MovementEntity();
        entity.setId("movement-123");

        MovementEntity savedEntity = new MovementEntity();
        savedEntity.setId("movement-123");

        Movement savedMovement = Movement.builder()
                .id("movement-123")
                .step(5)
                .build();

        when(mapper.toEntity(movement)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedMovement);

        Movement result = movementService.save(movement);

        assertNotNull(result);
        assertEquals("movement-123", result.getId());
        
        verify(mapper, times(1)).toEntity(movement);
        verify(jpaRepository, times(1)).save(entity);
        verify(mapper, times(1)).toDomain(savedEntity);
    }

    @Test
    @DisplayName("deleteById debe invocar el repository correctamente")
    void shouldDeleteById() {
        String movementId = "movement-to-delete";

        doNothing().when(jpaRepository).deleteById(movementId);

        movementService.deleteById(movementId);

        verify(jpaRepository, times(1)).deleteById(movementId);
    }

    @Test
    @DisplayName("countByAttemptId debe retornar el conteo correcto")
    void shouldReturnCorrectCount() {
        String attemptId = "attempt-123";

        when(jpaRepository.countByAttemptId(attemptId)).thenReturn(15L);

        long result = movementService.countByAttemptId(attemptId);

        assertEquals(15L, result);
        
        verify(jpaRepository, times(1)).countByAttemptId(attemptId);
    }

    @Test
    @DisplayName("countByAttemptId debe retornar 0 cuando no hay movimientos")
    void shouldReturnZeroWhenNoMovements() {
        String attemptId = "attempt-without-movements";

        when(jpaRepository.countByAttemptId(attemptId)).thenReturn(0L);

        long result = movementService.countByAttemptId(attemptId);

        assertEquals(0L, result);
        
        verify(jpaRepository, times(1)).countByAttemptId(attemptId);
    }
}
