package com.safora.server.services;

import com.safora.server.dtos.RouteCalculationRequest;
import com.safora.server.dtos.RouteCalculationResponse;
import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.services.providers.RouteProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrator service that ties together the Context Intelligence Engine.
 */
@Service
public class RouteRecommendationService {
    private final RouteProvider routeProvider;
    private final LightingService lightingService;
    private final CommunityReportService communityReportService;
    private final EmergencyZoneService emergencyZoneService;
    private final ConfidenceCalculatorService confidenceCalculator;
    private final ExplainabilityService explainabilityService;
    private final RouteRankerService routeRanker;

    public RouteRecommendationService(RouteProvider routeProvider,
                                      LightingService lightingService,
                                      CommunityReportService communityReportService,
                                      EmergencyZoneService emergencyZoneService,
                                      ConfidenceCalculatorService confidenceCalculator,
                                      ExplainabilityService explainabilityService,
                                      RouteRankerService routeRanker) {
        this.routeProvider = routeProvider;
        this.lightingService = lightingService;
        this.communityReportService = communityReportService;
        this.emergencyZoneService = emergencyZoneService;
        this.confidenceCalculator = confidenceCalculator;
        this.explainabilityService = explainabilityService;
        this.routeRanker = routeRanker;
    }

    public RouteCalculationResponse calculateBestRoutes(RouteCalculationRequest request) {
        // 1. Fetch raw un-scored routes
        List<RouteOptionDto> rawRoutes = routeProvider.fetchPossibleRoutes(request);

        // 2. Evaluate each route
        for (RouteOptionDto route : rawRoutes) {
            int lightScore = lightingService.evaluateLightingScore(route);
            int reportScore = communityReportService.evaluateSafetyScore(route);
            int emergencyScore = emergencyZoneService.evaluateProximityScore(route);

            // 3. Calculate Confidence Score
            int confidence = confidenceCalculator.calculateFinalConfidence(lightScore, reportScore, emergencyScore, request.getTimeOfDay());
            route.setConfidenceScore(confidence);
            route.setRiskLevel(confidenceCalculator.determineRiskLevel(confidence));

            // 4. Generate Explanations
            List<String> explanations = explainabilityService.generateExplanations(lightScore, reportScore, emergencyScore);
            route.setExplanations(explanations);
        }

        // 5. Rank and return
        return routeRanker.rankRoutes(rawRoutes);
    }
}
