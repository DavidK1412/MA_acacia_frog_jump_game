package com.acacia.app.domain.entity.game.movement;

import com.acacia.app.domain.entity.Game;
import com.acacia.app.domain.entity.game.attempt.GameAttempt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    
    private String id;
    private GameAttempt attempt;
    private LocalTime movementTime;
    private Integer step;
    private String movement;
    private Boolean isCorrect;
    private Boolean interruption;

    public void markAsCorrect() {
        this.isCorrect = true;
    }
    

    public void markAsIncorrect() {
        this.isCorrect = false;
    }

    public void markAsInterruption() {
        this.interruption = true;
    }
    

    public boolean wasCorrect() {
        return isCorrect != null && isCorrect;
    }
    

    public boolean wasInterrupted() {
        return interruption != null && interruption;
    }
    

    public String getAttemptId() {
        return attempt != null ? attempt.getId() : null;
    }
    

    public Game getGame() {
        return attempt != null ? attempt.getGame() : null;
    }
}
