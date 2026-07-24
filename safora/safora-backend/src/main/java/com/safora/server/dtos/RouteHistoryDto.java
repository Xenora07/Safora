package com.safora.server.dtos;

import com.safora.server.enums.TravelMode;
import java.time.LocalDateTime;

public class RouteHistoryDto {
    private Long id;
    private Double sourceLat;
    private Double sourceLng;
    private Double destLat;
    private Double destLng;
    private TravelMode travelMode;
    private String recommendedRouteId;
    private int finalConfidenceScore;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getSourceLat() { return sourceLat; }
    public void setSourceLat(Double sourceLat) { this.sourceLat = sourceLat; }
    public Double getSourceLng() { return sourceLng; }
    public void setSourceLng(Double sourceLng) { this.sourceLng = sourceLng; }
    public Double getDestLat() { return destLat; }
    public void setDestLat(Double destLat) { this.destLat = destLat; }
    public Double getDestLng() { return destLng; }
    public void setDestLng(Double destLng) { this.destLng = destLng; }
    public TravelMode getTravelMode() { return travelMode; }
    public void setTravelMode(TravelMode travelMode) { this.travelMode = travelMode; }
    public String getRecommendedRouteId() { return recommendedRouteId; }
    public void setRecommendedRouteId(String recommendedRouteId) { this.recommendedRouteId = recommendedRouteId; }
    public int getFinalConfidenceScore() { return finalConfidenceScore; }
    public void setFinalConfidenceScore(int finalConfidenceScore) { this.finalConfidenceScore = finalConfidenceScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
