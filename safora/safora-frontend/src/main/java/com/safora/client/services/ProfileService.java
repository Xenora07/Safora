package com.safora.client.services;

import com.safora.client.api.ApiClient;
import com.safora.client.dto.EmergencyContactDto;
import com.safora.client.dto.UserProfileDto;

import java.util.concurrent.CompletableFuture;

public class ProfileService {
    
    public static CompletableFuture<UserProfileDto> getProfile() {
        if (AuthService.isDemoMode()) {
            return CompletableFuture.completedFuture(MockDataProvider.getMockProfile());
        }
        return ApiClient.get("/profile", UserProfileDto.class);
    }
    
    public static CompletableFuture<com.safora.client.dto.RouteHistoryDto[]> getJourneyHistory() {
        if (AuthService.isDemoMode()) {
            return CompletableFuture.completedFuture(MockDataProvider.getMockJourneyHistory());
        }
        return ApiClient.get("/routes/history", com.safora.client.dto.RouteHistoryDto[].class);
    }
    
    public static CompletableFuture<EmergencyContactDto> addEmergencyContact(EmergencyContactDto dto) {
        if (AuthService.isDemoMode()) {
            dto.setId(System.currentTimeMillis());
            return CompletableFuture.completedFuture(dto);
        }
        return ApiClient.post("/profile/contacts", dto, EmergencyContactDto.class);
    }
    
    public static CompletableFuture<EmergencyContactDto> editEmergencyContact(Long id, EmergencyContactDto dto) {
        if (AuthService.isDemoMode()) {
            dto.setId(id);
            return CompletableFuture.completedFuture(dto);
        }
        return ApiClient.put("/profile/contacts/" + id, dto, EmergencyContactDto.class);
    }
    
    public static CompletableFuture<Void> deleteEmergencyContact(Long id) {
        if (AuthService.isDemoMode()) {
            return CompletableFuture.completedFuture(null);
        }
        return ApiClient.delete("/profile/contacts/" + id);
    }
}
