package com.safora.server.dtos;

import java.util.List;

public class RouteCalculationResponse {
    private RouteOptionDto recommendedRoute;
    private List<RouteOptionDto> alternativeRoutes;

    // Getters and Setters
    public RouteOptionDto getRecommendedRoute() { return recommendedRoute; }
    public void setRecommendedRoute(RouteOptionDto recommendedRoute) { this.recommendedRoute = recommendedRoute; }
    public List<RouteOptionDto> getAlternativeRoutes() { return alternativeRoutes; }
    public void setAlternativeRoutes(List<RouteOptionDto> alternativeRoutes) { this.alternativeRoutes = alternativeRoutes; }
}
