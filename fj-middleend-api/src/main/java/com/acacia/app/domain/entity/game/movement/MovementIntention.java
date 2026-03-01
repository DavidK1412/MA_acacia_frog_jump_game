package com.acacia.app.domain.entity.game.movement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MovementIntention {
    private List<Integer>  movement;
    private String gameId;
}
