package com.acacia.app.domain.entity.game.movement;

import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio que representa los errores acumulados en un intento de juego.
 * Registra cuántos errores ha tenido el jugador en un intento específico.
 * 
 * Usa composición de objetos en lugar de IDs para mantener un modelo de dominio rico.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovementMiss {
    
    private String id;
    private GameAttempt gameAttempt;
    private Integer count;

    public void incrementCount() {
        if (this.count == null) {
            this.count = 1;
        } else {
            this.count++;
        }
    }
    

    public int getMissCount() {
        return count != null ? count : 0;
    }
    

    public String getGameAttemptId() {
        return gameAttempt != null ? gameAttempt.getId() : null;
    }

    public boolean isAttemptActive() {
        return gameAttempt != null && gameAttempt.isCurrentlyActive();
    }
}
