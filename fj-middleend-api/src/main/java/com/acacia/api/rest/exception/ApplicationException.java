package com.acacia.api.rest.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    
    private final int statusCode;
    private final String errorCode;
    
    public ApplicationException(String message) {
        super(message);
        this.statusCode = 500;
        this.errorCode = "INTERNAL_SERVER_ERROR";
    }
    
    public ApplicationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = getErrorCodeFromStatus(statusCode);
    }
    
    public ApplicationException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.errorCode = "INTERNAL_SERVER_ERROR";
    }
    
    public ApplicationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = getErrorCodeFromStatus(statusCode);
    }
    
    private String getErrorCodeFromStatus(int status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT";
            case 422 -> "UNPROCESSABLE_ENTITY";
            case 500 -> "INTERNAL_SERVER_ERROR";
            case 503 -> "SERVICE_UNAVAILABLE";
            default -> "ERROR";
        };
    }
}
