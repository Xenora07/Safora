package com.safora.client.services;

import com.safora.client.api.ApiClient;
import com.safora.client.dto.CreateReportRequest;
import com.safora.client.dto.NearbyReportResponse;
import com.safora.client.dto.SafetyReportResponse;

import java.util.concurrent.CompletableFuture;

public class CommunityReportService {
    
    public static CompletableFuture<SafetyReportResponse> submitReport(CreateReportRequest request) {
        return ApiClient.post("/reports", request, SafetyReportResponse.class);
    }
    
    public static CompletableFuture<NearbyReportResponse[]> getNearbyReports(double lat, double lng, double radiusKm) {
        String url = String.format("/reports/nearby?lat=%s&lng=%s&radiusKm=%s", lat, lng, radiusKm);
        return ApiClient.get(url, NearbyReportResponse[].class);
    }
}
