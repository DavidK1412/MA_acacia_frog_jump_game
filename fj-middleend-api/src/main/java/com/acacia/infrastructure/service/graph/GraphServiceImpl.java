package com.acacia.infrastructure.service.graph;

import com.acacia.infrastructure.client.dto.graph.NextMoveOptions;
import com.acacia.infrastructure.client.dto.graph.NextMoveRequest;
import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;
import com.acacia.app.domain.entity.game.decision.Decision;
import com.acacia.app.domain.services.external.graph.GraphService;
import com.acacia.infrastructure.client.graph.GraphApiClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class GraphServiceImpl implements GraphService {
    
    private final GraphApiClient graphApiClient;

    public NextMoveResponse getBestNextFromGraph(List<Integer> state, String goal) {
        try {
            NextMoveRequest request = new NextMoveRequest(
                    state,
                    goal,
                    new NextMoveOptions(true, true)
            );

            NextMoveResponse response = graphApiClient.getNextMove(request);
            return response;
        } catch (HttpClientResponseException e) {
            log.error("Error calling graph API: status={}, message={}",
                    e.getStatus(), e.getMessage());
            throw new GraphApiException("Failed to get next move from graph API", e);
        } catch (Exception e) {
            log.error("Unexpected error calling graph API", e);
            throw new GraphApiException("Unexpected error communicating with graph API", e);
        }
    }
}
