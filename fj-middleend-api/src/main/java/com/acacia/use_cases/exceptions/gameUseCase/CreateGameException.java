package com.acacia.use_cases.exceptions.gameUseCase;

import com.acacia.api.rest.exception.ApplicationException;

public class CreateGameException extends ApplicationException {
    
    public CreateGameException(String message) {
        super(message, 422, "CREATE_GAME_FAILED");
    }
}
