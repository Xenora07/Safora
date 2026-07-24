package com.safora.server.entities;

import com.safora.server.enums.TravelMode;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_history", indexes = {
    @Index(name = "idx_route_user_id", columnList = "userId")
})
public class RouteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Double sourceLat;
    private Double sourceLng;
    private Double destLat;
    private Double destLng;

    @Enumerated(EnumType.STRING)
    private TravelMode travelMode;

    private String recommendedRouteId;
    private int finalConfidenceScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
