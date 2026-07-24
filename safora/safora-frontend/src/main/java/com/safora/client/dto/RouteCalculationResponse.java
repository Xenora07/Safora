package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteCalculationResponse {
    private RouteOptionDto recommendedRoute;
    private List<RouteOptionDto> alternativeRoutes;

    public RouteCalculationResponse() {}

    public RouteOptionDto getRecommendedRoute() { return recommendedRoute; }
    public void setRecommendedRoute(RouteOptionDto recommendedRoute) { this.recommendedRoute = recommendedRoute; }
    public List<RouteOptionDto> getAlternativeRoutes() { return alternativeRoutes; }
    public void setAlternativeRoutes(List<RouteOptionDto> alternativeRoutes) { this.alternativeRoutes = alternativeRoutes; }
}
