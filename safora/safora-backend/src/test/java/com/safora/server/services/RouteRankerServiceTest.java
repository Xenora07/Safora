package com.safora.server.services;

import com.safora.server.dtos.RouteCalculationResponse;
import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.enums.RouteType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteRankerServiceTest {

    private final RouteRankerService rankerService = new RouteRankerService();

    @Test
    void testRankRoutes() {
        RouteOptionDto r1 = new RouteOptionDto();
        r1.setRouteId("1");
        r1.setConfidenceScore(70);
        r1.setEstimatedTimeMins(10);

        RouteOptionDto r2 = new RouteOptionDto();
        r2.setRouteId("2");
        r2.setConfidenceScore(95);
        r2.setEstimatedTimeMins(15);

        RouteOptionDto r3 = new RouteOptionDto();
        r3.setRouteId("3");
        r3.setConfidenceScore(60);
        r3.setEstimatedTimeMins(8); // Fastest

        List<RouteOptionDto> routes = Arrays.asList(r1, r2, r3);
        RouteCalculationResponse response = rankerService.rankRoutes(routes);

        assertEquals("2", response.getRecommendedRoute().getRouteId());
        assertEquals(RouteType.SAFE, response.getRecommendedRoute().getRouteType());

        RouteOptionDto altFastest = response.getAlternativeRoutes().stream()
                .filter(r -> r.getRouteType() == RouteType.FASTEST).findFirst().get();
        assertEquals("3", altFastest.getRouteId());
    }
}
