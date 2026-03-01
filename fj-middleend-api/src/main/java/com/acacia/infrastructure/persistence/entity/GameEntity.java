package com.acacia.infrastructure.persistence.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
public class GameEntity {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "is_finished", nullable = false)
    private Boolean isFinished;
    
    @Column(name = "buclicity_avg")
    private Float buclicityAvg;
    
    @Column(name = "branch_factor_avg")
    private Float branchFactorAvg;
}
