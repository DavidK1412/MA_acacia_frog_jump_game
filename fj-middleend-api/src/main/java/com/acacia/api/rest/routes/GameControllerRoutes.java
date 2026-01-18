package com.acacia.api.rest.routes;

public final class GameControllerRoutes {
    public static final String BASE = "/game";
    public static final String REGISTER_MOVEMENT = "/{gameId}";
    public static final String BEST_NEXT_MOVE = "/{gameId}/best_next";
    public static final String REGISTER_MISS = "/{gameId}/miss";
}
