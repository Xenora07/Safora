package com.safora.client.services;

import com.safora.client.api.ApiClient;
import com.safora.client.dto.RouteCalculationRequest;
import com.safora.client.dto.RouteCalculationResponse;

import java.util.concurrent.CompletableFuture;

public class RouteService {
    
    public static CompletableFuture<RouteCalculationResponse> calculateRoutes(RouteCalculationRequest request) {
        if (AuthService.isDemoMode()) {
            return CompletableFuture.completedFuture(MockDataProvider.getMockRouteRecommendation());
        }
        return ApiClient.post("/routes/calculate", request, RouteCalculationResponse.class);
    }
}
