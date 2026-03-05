package com.acacia.infrastructure.client.graph;

import com.acacia.infrastructure.client.dto.graph.NextMoveRequest;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "graph-api")
public interface GraphApiClient {
    
    @Post("/v1/graph/next-move")
    NextMoveResponse getNextMove(@Body NextMoveRequest request);
}
