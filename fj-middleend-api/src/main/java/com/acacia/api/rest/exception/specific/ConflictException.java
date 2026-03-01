package com.acacia.api.rest.exception.specific;

import com.acacia.api.rest.exception.ApplicationException;

public class ConflictException extends ApplicationException {
    
    public ConflictException(String message) {
        super(message, 409, "CONFLICT");
    }
}
