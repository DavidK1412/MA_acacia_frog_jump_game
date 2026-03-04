package com.acacia.app.domain.services.external.graph;

import com.acacia.app.domain.entity.game.decision.Decision;

public interface GraphService {
    Decision getBestNext(String gameId);
}
