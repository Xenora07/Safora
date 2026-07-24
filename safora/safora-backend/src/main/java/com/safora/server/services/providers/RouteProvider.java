package com.safora.server.services.providers;

import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.dtos.RouteCalculationRequest;
import java.util.List;

public interface RouteProvider {
    /**
     * Generates multiple possible routes for a given request.
     * The returned routes are un-scored raw paths.
     */
    List<RouteOptionDto> fetchPossibleRoutes(RouteCalculationRequest request);
}
