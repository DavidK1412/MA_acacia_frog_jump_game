package com.acacia.app.domain.entity.game.difficulty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio que representa el nivel de dificultad del juego.
 * Esta entidad es agnóstica a la infraestructura y contiene solo lógica de negocio.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Difficulty {
    
    private Integer id;
    private String name;
    private Integer numberOfBlocks;
    
    /**
     * Valida que la dificultad tenga un número de bloques válido.
     * @return true si es válida, false en caso contrario
     */
    public boolean isValid() {
        return numberOfBlocks != null && numberOfBlocks > 0 && name != null && !name.isBlank();
    }
}
