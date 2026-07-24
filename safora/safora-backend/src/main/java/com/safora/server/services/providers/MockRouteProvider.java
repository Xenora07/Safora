package com.safora.server.services.providers;

import com.safora.server.dtos.RouteCalculationRequest;
import com.safora.server.dtos.RouteOptionDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class MockRouteProvider implements RouteProvider {

    @Override
    public List<RouteOptionDto> fetchPossibleRoutes(RouteCalculationRequest request) {
        // In a real implementation, this would call OSRM, GraphHopper, or Google Directions API
        // Here we mock 3 distinct physical paths
        
        RouteOptionDto route1 = new RouteOptionDto();
        route1.setRouteId(UUID.randomUUID().toString());
        route1.setDistanceKm(5.2);
        route1.setEstimatedTimeMins(12);
        route1.setPolylineMock("Path A (Main Highway)");

        RouteOptionDto route2 = new RouteOptionDto();
        route2.setRouteId(UUID.randomUUID().toString());
        route2.setDistanceKm(4.8);
        route2.setEstimatedTimeMins(15);
        route2.setPolylineMock("Path B (Inner City Streets)");

        RouteOptionDto route3 = new RouteOptionDto();
        route3.setRouteId(UUID.randomUUID().toString());
        route3.setDistanceKm(6.1);
        route3.setEstimatedTimeMins(14);
        route3.setPolylineMock("Path C (Bypass)");

        return Arrays.asList(route1, route2, route3);
    }
}
