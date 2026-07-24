package com.safora.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String message;

    public AuthResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
