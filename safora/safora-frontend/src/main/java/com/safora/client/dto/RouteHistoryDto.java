package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteHistoryDto {
    private Long id;
    private double sourceLat;
    private double sourceLng;
    private double destLat;
    private double destLng;
    private String travelMode;
    private String recommendedRouteId;
    private int finalConfidenceScore;
    private LocalDateTime createdAt;

    public RouteHistoryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getSourceLat() { return sourceLat; }
    public void setSourceLat(double sourceLat) { this.sourceLat = sourceLat; }
    public double getSourceLng() { return sourceLng; }
    public void setSourceLng(double sourceLng) { this.sourceLng = sourceLng; }
    public double getDestLat() { return destLat; }
    public void setDestLat(double destLat) { this.destLat = destLat; }
    public double getDestLng() { return destLng; }
    public void setDestLng(double destLng) { this.destLng = destLng; }
    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
    public String getRecommendedRouteId() { return recommendedRouteId; }
    public void setRecommendedRouteId(String recommendedRouteId) { this.recommendedRouteId = recommendedRouteId; }
    public int getFinalConfidenceScore() { return finalConfidenceScore; }
    public void setFinalConfidenceScore(int finalConfidenceScore) { this.finalConfidenceScore = finalConfidenceScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
