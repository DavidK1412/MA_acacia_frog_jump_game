package com.acacia.app.domain.entity.game.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameCreateOutput {
    private String gameId;
    private String message;
}
