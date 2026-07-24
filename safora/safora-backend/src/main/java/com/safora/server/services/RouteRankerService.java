package com.safora.server.services;

import com.safora.server.dtos.RouteCalculationResponse;
import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.enums.RouteType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteRankerService {

    public RouteCalculationResponse rankRoutes(List<RouteOptionDto> routes) {
        if (routes == null || routes.isEmpty()) {
            return new RouteCalculationResponse();
        }

        // Sort by confidence score descending
        List<RouteOptionDto> sortedRoutes = routes.stream()
                .sorted(Comparator.comparing(RouteOptionDto::getConfidenceScore).reversed())
                .collect(Collectors.toList());

        // Assign route types based on simple heuristic for this module
        RouteOptionDto safest = sortedRoutes.get(0);
        safest.setRouteType(RouteType.SAFE);

        // Find fastest among the remaining
        List<RouteOptionDto> remaining = new ArrayList<>(sortedRoutes.subList(1, sortedRoutes.size()));
        RouteOptionDto fastest = remaining.stream()
                .min(Comparator.comparing(RouteOptionDto::getEstimatedTimeMins))
                .orElse(null);

        if (fastest != null) {
            fastest.setRouteType(RouteType.FASTEST);
            remaining.remove(fastest);
        }

        // Any remaining is considered balanced/alternative
        for (RouteOptionDto route : remaining) {
            route.setRouteType(RouteType.BALANCED);
        }

        RouteCalculationResponse response = new RouteCalculationResponse();
        response.setRecommendedRoute(safest);
        
        List<RouteOptionDto> alternatives = new ArrayList<>();
        if (fastest != null) alternatives.add(fastest);
        alternatives.addAll(remaining);
        
        response.setAlternativeRoutes(alternatives);
        
        return response;
    }
}
