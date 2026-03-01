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
@Table(name = "movements_misses")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
public class MovementMissEntity {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "game_attempt_id", nullable = false, length = 36)
    private String gameAttemptId;
    
    @Column(name = "count", nullable = false)
    private Integer count;
    
    @ManyToOne
    @JoinColumn(name = "game_attempt_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_movements_misses_game_attempts"))
    private GameAttemptEntity gameAttempt;
}
