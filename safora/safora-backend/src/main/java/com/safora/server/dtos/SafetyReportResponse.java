package com.safora.server.dtos;

import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;
import com.safora.server.enums.ReportStatus;
import java.time.LocalDateTime;

public class SafetyReportResponse {
    private Long id;
    private Long userId;
    private Double latitude;
    private Double longitude;
    private ReportCategory category;
    private String description;
    private ReportSeverity severity;
    private ReportStatus status;
    private LocalDateTime timestamp;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
