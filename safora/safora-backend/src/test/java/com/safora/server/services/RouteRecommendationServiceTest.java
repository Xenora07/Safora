package com.safora.server.services;

import com.safora.server.config.SafetyWeightsConfig;
import com.safora.server.config.SafetyWeightsConfig;
import com.safora.server.dtos.RouteCalculationRequest;
import com.safora.server.dtos.RouteCalculationResponse;
import com.safora.server.enums.RouteType;
import com.safora.server.repositories.MockSafetyDataRepository;
import com.safora.server.repositories.SafetyReportRepository;
import com.safora.server.services.providers.MockRouteProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RouteRecommendationServiceTest {

    private RouteRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockSafetyDataRepository repo = new MockSafetyDataRepository();
        SafetyWeightsConfig config = new SafetyWeightsConfig();
        
        SafetyReportRepository reportRepo = mock(SafetyReportRepository.class);
        when(reportRepo.count()).thenReturn(2L);

        recommendationService = new RouteRecommendationService(
                new MockRouteProvider(),
                new LightingService(repo),
                new CommunityReportService(reportRepo),
                new EmergencyZoneService(repo),
                new ConfidenceCalculatorService(config),
                new ExplainabilityService(),
                new RouteRankerService()
        );
    }

    @Test
    void testCalculateBestRoutes_EndToEndFlow() {
        RouteCalculationRequest req = new RouteCalculationRequest();
        req.setSourceLat(10.0);
        req.setSourceLng(10.0);
        req.setDestLat(20.0);
        req.setDestLng(20.0);
        req.setTimeOfDay(LocalTime.of(14, 0));

        RouteCalculationResponse response = recommendationService.calculateBestRoutes(req);

        assertNotNull(response);
        assertNotNull(response.getRecommendedRoute());
        assertEquals(RouteType.SAFE, response.getRecommendedRoute().getRouteType());
        assertEquals(2, response.getAlternativeRoutes().size());
        assertNotNull(response.getRecommendedRoute().getExplanations());
    }
}
