package com.acacia.app.domain.entity.game.decision;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class Decision {
    private String type;
    private Map<String, Object> actions;
}
