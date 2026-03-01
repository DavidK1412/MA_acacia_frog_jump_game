package com.acacia.api.rest.exception.specific;

import com.acacia.api.rest.exception.ApplicationException;

public class BadRequestException extends ApplicationException {
    
    public BadRequestException(String message) {
        super(message, 400, "BAD_REQUEST");
    }
}
