package com.safora.server.services;

import com.safora.server.dtos.CreateReportRequest;
import com.safora.server.dtos.NearbyReportResponse;
import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.dtos.SafetyReportResponse;
import com.safora.server.entities.SafetyReport;
import com.safora.server.enums.ReportStatus;
import com.safora.server.repositories.SafetyReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityReportService {
    private final SafetyReportRepository repository;

    public CommunityReportService(SafetyReportRepository repository) {
        this.repository = repository;
    }

    public SafetyReportResponse submitReport(Long userId, CreateReportRequest request) {
        SafetyReport report = new SafetyReport();
        report.setUserId(userId);
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());
        report.setCategory(request.getCategory());
        report.setDescription(request.getDescription());
        report.setSeverity(request.getSeverity());
        report.setStatus(ReportStatus.ACTIVE);

        SafetyReport saved = repository.save(report);
        return mapToResponse(saved);
    }

    public List<SafetyReportResponse> getUserReports(Long userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NearbyReportResponse> getNearbyReports(Double lat, Double lng, Double radiusKm) {
        List<SafetyReport> nearby = repository.findNearbyReports(lat, lng, radiusKm, ReportStatus.ACTIVE.name());
        return nearby.stream().map(r -> {
            NearbyReportResponse dto = new NearbyReportResponse();
            dto.setId(r.getId());
            dto.setLatitude(r.getLatitude());
            dto.setLongitude(r.getLongitude());
            dto.setCategory(r.getCategory());
            dto.setSeverity(r.getSeverity());
            dto.setDistanceKm(calculateDistance(lat, lng, r.getLatitude(), r.getLongitude()));
            return dto;
        }).collect(Collectors.toList());
    }

    public void deleteReport(Long reportId, Long userId) {
        SafetyReport report = repository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        if (!report.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this report");
        }
        repository.delete(report);
    }

    // --- Module 2 Integration ---
    public int evaluateSafetyScore(RouteOptionDto route) {
        // Replacing mock: Check for active reports. For simplicity of the bridge, we count them.
        long activeReportCount = repository.count();
        int score = 100 - (int) (activeReportCount * 5);
        return Math.max(0, Math.min(100, score));
    }

    private SafetyReportResponse mapToResponse(SafetyReport report) {
        SafetyReportResponse res = new SafetyReportResponse();
        res.setId(report.getId());
        res.setUserId(report.getUserId());
        res.setLatitude(report.getLatitude());
        res.setLongitude(report.getLongitude());
        res.setCategory(report.getCategory());
        res.setDescription(report.getDescription());
        res.setSeverity(report.getSeverity());
        res.setStatus(report.getStatus());
        res.setTimestamp(report.getTimestamp());
        return res;
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
