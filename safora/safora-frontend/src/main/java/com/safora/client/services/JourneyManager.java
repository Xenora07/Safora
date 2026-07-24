package com.safora.client.services;

import com.safora.client.dto.CompletedJourneyDto;
import com.safora.client.dto.RouteOptionDto;
import java.time.LocalDateTime;

public class JourneyManager {
    private static RouteOptionDto selectedRoute;
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;
    private static String destination;
    private static String source;
    private static String journeyStatus;
    private static String transportMode = "Drive"; // Default: Drive, Walk, Bike, Transit

    public static void setTransportMode(String mode) {
        transportMode = mode;
    }

    public static String getTransportMode() {
        return transportMode;
    }

    public static void startJourney(RouteOptionDto route) {
        selectedRoute = route;
        startTime = LocalDateTime.now();
        endTime = null;
        journeyStatus = "ACTIVE";
    }

    public static void setDestination(String dest) {
        destination = dest;
    }
    
    public static String getDestination() {
        return destination;
    }
    
    public static void setSource(String src) {
        source = src;
    }
    
    public static String getSource() {
        return source;
    }

    public static void endJourney() {
        endTime = LocalDateTime.now();
        journeyStatus = "COMPLETED";
    }

    public static CompletedJourneyDto getCompletedJourney() {
        return new CompletedJourneyDto(selectedRoute, destination, startTime, endTime);
    }
    
    public static RouteOptionDto getSelectedRoute() {
        return selectedRoute;
    }
}
