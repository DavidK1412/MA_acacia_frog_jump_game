package com.acacia.app.domain.services.external.graph;

import com.acacia.infrastructure.client.dto.graph.NextMoveResponse;

import java.util.List;

public interface GraphService {
    NextMoveResponse getBestNextFromGraph(List<Integer> state, String goal);
}
