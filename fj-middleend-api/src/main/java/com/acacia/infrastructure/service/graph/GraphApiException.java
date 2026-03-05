package com.acacia.infrastructure.service.graph;

import com.acacia.api.rest.exception.ApplicationException;

public class GraphApiException extends ApplicationException {
    
    public GraphApiException(String message) {
        super(message, 503, "GRAPH_API_ERROR");
    }
    
    public GraphApiException(String message, Throwable cause) {
        super(message, 503, cause);
    }
}
