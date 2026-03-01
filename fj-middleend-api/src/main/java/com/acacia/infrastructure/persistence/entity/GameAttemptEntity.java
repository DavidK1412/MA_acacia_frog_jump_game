package com.acacia.infrastructure.persistence.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_attempts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
public class GameAttemptEntity {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "game_id", nullable = false, length = 36)
    private String gameId;
    
    @Column(name = "difficulty_id", nullable = false)
    private Integer difficultyId;
    
    @Column(name = "last_buclicity", nullable = false)
    private Float lastBuclicity;
    
    @Column(name = "last_branch_factor", nullable = false)
    private Float lastBranchFactor;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id", 
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_game_attempts_game"))
    private GameEntity game;
    
    @ManyToOne
    @JoinColumn(name = "difficulty_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_game_attempts_difficulty"))
    private DifficultyEntity difficulty;
}
