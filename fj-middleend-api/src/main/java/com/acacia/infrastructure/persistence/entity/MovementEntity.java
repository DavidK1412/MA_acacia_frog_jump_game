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

import java.time.LocalTime;

@Entity
@Table(name = "movements")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
public class MovementEntity {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "attempt_id", nullable = false, length = 36)
    private String attemptId;
    
    @Column(name = "movement_time", columnDefinition = "TIME(0)")
    private LocalTime movementTime;
    
    @Column(name = "step", nullable = false)
    private Integer step;
    
    @Column(name = "movement", length = 20)
    private String movement;
    
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
    
    @Column(name = "interuption", nullable = false)
    private Boolean interruption;
    
    @ManyToOne
    @JoinColumn(name = "attempt_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_movements_game_attempts"))
    private GameAttemptEntity gameAttempt;
}
