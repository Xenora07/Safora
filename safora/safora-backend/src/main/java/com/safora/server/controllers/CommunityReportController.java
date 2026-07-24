package com.safora.server.controllers;

import com.safora.server.dtos.CreateReportRequest;
import com.safora.server.dtos.NearbyReportResponse;
import com.safora.server.dtos.SafetyReportResponse;
import com.safora.server.services.CommunityReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class CommunityReportController {

    private final CommunityReportService reportService;

    public CommunityReportController(CommunityReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<SafetyReportResponse> submitReport(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @Valid @RequestBody CreateReportRequest request) {
        SafetyReportResponse response = reportService.submitReport(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SafetyReportResponse>> getUserReports(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return ResponseEntity.ok(reportService.getUserReports(userId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyReportResponse>> getNearbyReports(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        return ResponseEntity.ok(reportService.getNearbyReports(lat, lng, radiusKm));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long id) {
        reportService.deleteReport(id, userId);
        return ResponseEntity.noContent().build();
    }
}
