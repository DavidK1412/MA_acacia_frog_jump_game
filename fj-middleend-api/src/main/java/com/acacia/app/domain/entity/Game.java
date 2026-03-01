package com.acacia.app.domain.entity;

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
public class Game {
    
    private String id;
    private Boolean isFinished;
    private Float buclicityAvg;
    private Float branchFactorAvg;

    public void finish() {
        this.isFinished = true;
    }

    public boolean isInProgress() {
        return !isFinished;
    }

    public void updateAverages(Float buclicity, Float branchFactor) {
        this.buclicityAvg = buclicity;
        this.branchFactorAvg = branchFactor;
    }
}
