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

    public class DecisionType {
        public static final String BEST_NEXT = "BEST_NEXT";
        public static final String ASK = "ASK";
        public static final String CORRECT = "CORRECT";
        public static final String TUTORIAL = "TUTORIAL";
        public static final String DIFFICULTY_CHANGE = "CHANGE_DIFF";
        public static final String SPEECH = "SPEECH";
    }
}
