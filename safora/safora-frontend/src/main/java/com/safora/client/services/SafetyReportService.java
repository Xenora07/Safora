package com.safora.client.services;

import com.safora.client.api.ApiClient;
import com.safora.client.dto.CreateReportRequest;
import com.safora.client.dto.SafetyReportResponse;

import java.util.concurrent.CompletableFuture;

public class SafetyReportService {
    
    public static CompletableFuture<SafetyReportResponse> createReport(CreateReportRequest request) {
        if (AuthService.isDemoMode()) {
            SafetyReportResponse mock = new SafetyReportResponse();
            mock.setId(System.currentTimeMillis());
            return CompletableFuture.completedFuture(mock);
        }
        return ApiClient.post("/reports", request, SafetyReportResponse.class);
    }
}
