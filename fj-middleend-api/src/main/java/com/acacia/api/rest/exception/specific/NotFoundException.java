package com.acacia.api.rest.exception.specific;

import com.acacia.api.rest.exception.ApplicationException;

public class NotFoundException extends ApplicationException {
    
    public NotFoundException(String message) {
        super(message, 404, "NOT_FOUND");
    }
    
    public NotFoundException(String resource, String id) {
        super(String.format("%s with id '%s' not found", resource, id), 404, "NOT_FOUND");
    }
}
