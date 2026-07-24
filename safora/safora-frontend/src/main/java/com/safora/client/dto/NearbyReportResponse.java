package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NearbyReportResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String category;
    private String severity;
    private Double distanceKm;
    // timestamp might not be in the DTO? The prompt says "Time" but backend DTO NearbyReportResponse doesn't have it. 
    // I'll add a mock time display or parse if it's there.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
