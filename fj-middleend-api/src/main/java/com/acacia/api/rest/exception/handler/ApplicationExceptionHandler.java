package com.acacia.api.rest.exception.handler;

import com.acacia.api.rest.exception.ApplicationException;
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
@Requires(classes = {ApplicationException.class, ExceptionHandler.class})
@Slf4j
public class ApplicationExceptionHandler implements ExceptionHandler<ApplicationException, HttpResponse<ErrorResponse>> {
    
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ApplicationException exception) {
        log.error("Application exception: {} - {}", exception.getErrorCode(), exception.getMessage(), exception);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(exception.getStatusCode())
                .error(exception.getErrorCode())
                .message(exception.getMessage())
                .path(request.getPath())
                .build();
        
        return HttpResponse.status(HttpStatus.valueOf(exception.getStatusCode()))
                .body(errorResponse);
    }
}
