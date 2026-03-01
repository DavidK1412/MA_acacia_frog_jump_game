package com.acacia.api.rest.exception.specific;

import com.acacia.api.rest.exception.ApplicationException;

public class UnauthorizedException extends ApplicationException {
    
    public UnauthorizedException(String message) {
        super(message, 401, "UNAUTHORIZED");
    }
    
    public UnauthorizedException() {
        super("Unauthorized access", 401, "UNAUTHORIZED");
    }
}
