package com.acacia.commons.state;

import java.util.List;

public class StateHelper {
    public static List<Integer> getDifficultyGoalState(Integer difficulty) {
        return switch (difficulty) {
            case 1 -> List.of(4, 5, 6, 0, 1, 2, 3);
            case 2 -> List.of(5, 6, 7, 8, 0, 1, 2, 3, 4);
            case 3 -> List.of(6, 7, 8, 9, 10, 0, 1, 2, 3, 4, 5);
            default -> throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        };
    }

    public static String getTypeOfGoalState(Integer difficulty) {
        return switch (difficulty) {
            case 1, 2, 3 -> "default";
            default -> throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        };
    }
}
