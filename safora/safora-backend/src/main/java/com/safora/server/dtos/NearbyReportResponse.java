package com.safora.server.dtos;

import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;

public class NearbyReportResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private ReportCategory category;
    private ReportSeverity severity;
    private Double distanceKm;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public ReportCategory getCategory() { return category; }
    public void setCategory(ReportCategory category) { this.category = category; }
    public ReportSeverity getSeverity() { return severity; }
    public void setSeverity(ReportSeverity severity) { this.severity = severity; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
