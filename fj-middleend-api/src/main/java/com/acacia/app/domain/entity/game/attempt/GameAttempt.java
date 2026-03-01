package com.acacia.app.domain.entity.game.attempt;

import com.acacia.app.domain.entity.game.difficulty.Difficulty;
import com.acacia.app.domain.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameAttempt {
    
    private String id;
    private Game game;
    private Difficulty difficulty;
    private Float lastBuclicity;
    private Float lastBranchFactor;
    private Boolean isActive;
    

    public void deactivate() {
        this.isActive = false;
    }
    

    public void activate() {
        this.isActive = true;
    }

    public void updateLastMetrics(Float buclicity, Float branchFactor) {
        this.lastBuclicity = buclicity;
        this.lastBranchFactor = branchFactor;
    }
    

    public boolean isCurrentlyActive() {
        return isActive != null && isActive;
    }

    public String getGameId() {
        return game != null ? game.getId() : null;
    }

    public Integer getDifficultyId() {
        return difficulty != null ? difficulty.getId() : null;
    }
    

    public Integer getNumberOfBlocks() {
        return difficulty != null ? difficulty.getNumberOfBlocks() : null;
    }
}
