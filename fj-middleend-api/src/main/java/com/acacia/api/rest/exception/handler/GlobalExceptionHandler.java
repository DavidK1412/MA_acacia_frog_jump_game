package com.acacia.api.rest.exception.handler;

import com.acacia.api.rest.exception.ErrorResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Produces
@Singleton
@Requires(classes = {Exception.class, ExceptionHandler.class})
@Slf4j
public class GlobalExceptionHandler implements ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {
    
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Exception exception) {
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .path(request.getPath())
                .build();
        
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
