package com.safora.server.dtos;

import com.safora.server.enums.RiskLevel;
import com.safora.server.enums.RouteType;

import java.util.List;

public class RouteOptionDto {
    private String routeId;
    private RouteType routeType;
    private int confidenceScore;
    private RiskLevel riskLevel;
    private double distanceKm;
    private int estimatedTimeMins;
    private List<String> explanations;
    
    // Internal use for mock calculating, not strictly needed for client but useful
    private String polylineMock;

    // Getters and Setters
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public RouteType getRouteType() { return routeType; }
    public void setRouteType(RouteType routeType) { this.routeType = routeType; }
    public int getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(int confidenceScore) { this.confidenceScore = confidenceScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public int getEstimatedTimeMins() { return estimatedTimeMins; }
    public void setEstimatedTimeMins(int estimatedTimeMins) { this.estimatedTimeMins = estimatedTimeMins; }
    public List<String> getExplanations() { return explanations; }
    public void setExplanations(List<String> explanations) { this.explanations = explanations; }
    public String getPolylineMock() { return polylineMock; }
    public void setPolylineMock(String polylineMock) { this.polylineMock = polylineMock; }
}
