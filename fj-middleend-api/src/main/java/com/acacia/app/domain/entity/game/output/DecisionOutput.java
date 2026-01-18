package com.acacia.app.domain.entity.game.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class DecisionOutput {
    private String type;
    private Map<String, Object> actions;
}
