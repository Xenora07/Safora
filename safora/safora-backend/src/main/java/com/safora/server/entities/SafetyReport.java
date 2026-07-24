package com.safora.server.entities;

import com.safora.server.enums.ReportCategory;
import com.safora.server.enums.ReportSeverity;
import com.safora.server.enums.ReportStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "safety_reports", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_location", columnList = "latitude, longitude")
})
public class SafetyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportCategory category;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Getters and Setters
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
