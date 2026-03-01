package com.acacia.use_cases.commons;

public enum Messages {
    GAME_CREATED("Bienvenido al mundo virtual de Acacia. Tu aventura en la escalera comienza!");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
