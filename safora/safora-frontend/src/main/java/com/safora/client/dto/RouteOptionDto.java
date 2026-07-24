package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteOptionDto {
    private String routeId;
    private String routeType;
    private int confidenceScore;
    private String riskLevel;
    private double distanceKm;
    private int estimatedTimeMins;
    private List<String> explanations;
    private List<List<Double>> polyline;

    public RouteOptionDto() {}

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public int getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(int confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public int getEstimatedTimeMins() { return estimatedTimeMins; }
    public void setEstimatedTimeMins(int estimatedTimeMins) { this.estimatedTimeMins = estimatedTimeMins; }
    public List<String> getExplanations() { return explanations; }
    public void setExplanations(List<String> explanations) { this.explanations = explanations; }
    public List<List<Double>> getPolyline() { return polyline; }
    public void setPolyline(List<List<Double>> polyline) { this.polyline = polyline; }
}
