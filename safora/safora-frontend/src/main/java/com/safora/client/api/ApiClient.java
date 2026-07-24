package com.safora.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safora.client.config.AppConfig;
import com.safora.client.session.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(AppConfig.REQUEST_TIMEOUT_SECONDS))
            .build();
    
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static <T> CompletableFuture<T> get(String endpoint, Class<T> responseType) {
        HttpRequest request = buildBaseRequest(endpoint).GET().build();
        return sendAsync(request, responseType);
    }

    public static <T> CompletableFuture<T> post(String endpoint, Object body, Class<T> responseType) {
        try {
            String jsonBody = MAPPER.writeValueAsString(body);
            HttpRequest request = buildBaseRequest(endpoint)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            return sendAsync(request, responseType);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public static <T> CompletableFuture<T> put(String endpoint, Object body, Class<T> responseType) {
        try {
            String jsonBody = MAPPER.writeValueAsString(body);
            HttpRequest request = buildBaseRequest(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            return sendAsync(request, responseType);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<Void> delete(String endpoint) {
        HttpRequest request = buildBaseRequest(endpoint).DELETE().build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(res -> null);
    }

    private static HttpRequest.Builder buildBaseRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.BACKEND_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        if (SessionManager.isLoggedIn()) {
            builder.header("X-User-Id", SessionManager.getUserId().toString());
            String token = SessionManager.getAuthToken();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }
        }
        return builder;
    }

    private static <T> CompletableFuture<T> sendAsync(HttpRequest request, Class<T> responseType) {
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException(response.body());
                    }
                    try {
                        return MAPPER.readValue(response.body(), responseType);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse response", e);
                    }
                });
    }
}
