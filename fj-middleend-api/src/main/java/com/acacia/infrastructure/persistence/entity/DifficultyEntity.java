package com.acacia.infrastructure.persistence.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "difficulty")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
public class DifficultyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "number_of_blocks", nullable = false)
    private Integer numberOfBlocks;
}
