package com.safora.client.location;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import com.safora.client.services.AuthService;

public class DesktopLocationProvider implements LocationProvider {

    private final ObjectProperty<Location> location = new SimpleObjectProperty<>(new Location(19.0441, 72.9103)); // Default fallback (Mumbai)
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public DesktopLocationProvider() {
        startTracking();
    }

    @Override
    public Location getCurrentLocation() {
        return location.get();
    }

    public void setCurrentLocation(Location loc) {
        Platform.runLater(() -> location.set(loc));
    }

    @Override
    public Location resolveDestination(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + encodedQuery))
                    .header("User-Agent", "Safora-Desktop-App")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            
            if (body.contains("\"lat\"") && body.contains("\"lon\"")) {
                String latStr = body.split("\"lat\":\"")[1].split("\"")[0];
                String lonStr = body.split("\"lon\":\"")[1].split("\"")[0];
                return new Location(Double.parseDouble(latStr), Double.parseDouble(lonStr));
            }
        } catch (Exception e) {}
        // Fallback offset
        return new Location(location.get().getLat() + 0.02, location.get().getLng() + 0.02);
    }
    
    public CompletableFuture<Location> resolveDestinationAsync(String query) {
        return CompletableFuture.supplyAsync(() -> resolveDestination(query));
    }
    
    public CompletableFuture<String> findNearestPOI(double lat, double lng, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String query = URLEncoder.encode(type, StandardCharsets.UTF_8.toString());
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("https://nominatim.openstreetmap.org/search?format=json&q=%s&lat=%.4f&lon=%.4f&limit=1", query, lat, lng)))
                        .header("User-Agent", "Safora-Desktop-App")
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                if (body.contains("\"name\":\"")) {
                    String name = body.split("\"name\":\"")[1].split("\"")[0];
                    if (!name.isEmpty()) return name;
                }
            } catch (Exception e) {}
            return null;
        });
    }
    
    public CompletableFuture<String> reverseGeocode(double lat, double lng) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%.4f&lon=%.4f&zoom=16", lat, lng)))
                        .header("User-Agent", "Safora-Desktop-App")
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                if (body.contains("\"name\":\"")) {
                    String name = body.split("\"name\":\"")[1].split("\"")[0];
                    if (!name.isEmpty()) return name;
                } else if (body.contains("\"road\":\"")) {
                    return body.split("\"road\":\"")[1].split("\"")[0];
                }
            } catch (Exception e) {}
            return null;
        });
    }

    public void startTracking() {
        if (AuthService.isDemoMode()) {
            Platform.runLater(() -> location.set(new Location(19.0441, 72.9103)));
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://ip-api.com/json/"))
                .header("Accept", "application/json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        if (body.contains("\"lat\":") && body.contains("\"lon\":")) {
                            String latStr = body.split("\"lat\":")[1].split(",")[0];
                            String lonStr = body.split("\"lon\":")[1].split(",")[0].replaceAll("}","");
                            double lat = Double.parseDouble(latStr.trim());
                            double lon = Double.parseDouble(lonStr.trim());
                            Platform.runLater(() -> location.set(new Location(lat, lon)));
                        }
                    } catch (Exception e) {}
                });
    }

    public ObjectProperty<Location> locationProperty() {
        return location;
    }
}
