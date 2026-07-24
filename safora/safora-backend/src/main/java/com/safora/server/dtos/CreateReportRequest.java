package com.safora.server.dtos;

import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReportRequest {
    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Category is required")
    private ReportCategory category;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Severity is required")
    private ReportSeverity severity;

    // Getters and setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public ReportCategory getCategory() { return category; }
    public void setCategory(ReportCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ReportSeverity getSeverity() { return severity; }
    public void setSeverity(ReportSeverity severity) { this.severity = severity; }
}
