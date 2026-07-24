package com.safora.client.services;

import com.safora.client.api.ApiClient;
import com.safora.client.dto.AuthResponse;
import com.safora.client.dto.LoginRequest;
import com.safora.client.dto.RegisterRequest;
import com.safora.client.session.SessionManager;

import java.util.concurrent.CompletableFuture;

public class AuthService {
    
    private static AuthResponse currentUser;
    private static boolean isDemoMode = false;
    
    public static AuthResponse getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isDemoMode() {
        return isDemoMode;
    }

    public static CompletableFuture<AuthResponse> login(String email, String password) {
        if ("demo@safora.com".equals(email) && "demo123".equals(password)) {
            isDemoMode = true;
            AuthResponse demoUser = new AuthResponse();
            demoUser.setUserId(999L);
            demoUser.setFullName("Nidhi");
            demoUser.setToken("mock-jwt-token-demo");
            currentUser = demoUser;
            SessionManager.setSession(demoUser.getUserId(), demoUser.getToken());
            return CompletableFuture.completedFuture(demoUser);
        }

        LoginRequest request = new LoginRequest(email, password);
        return ApiClient.post("/auth/login", request, AuthResponse.class)
            .thenApply(response -> {
                currentUser = response;
                isDemoMode = false;
                if (response.getToken() != null) {
                    SessionManager.setSession(response.getUserId(), response.getToken());
                }
                return response;
            });
    }

    public static CompletableFuture<AuthResponse> register(String fullName, String email, String password, String phone) {
        RegisterRequest request = new RegisterRequest(fullName, email, password, phone);
        return ApiClient.post("/auth/register", request, AuthResponse.class)
            .thenApply(response -> {
                currentUser = response;
                isDemoMode = false;
                if (response.getToken() != null) {
                    SessionManager.setSession(response.getUserId(), response.getToken());
                }
                return response;
            });
    }
}
