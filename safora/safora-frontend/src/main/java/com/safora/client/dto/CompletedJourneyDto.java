package com.safora.client.dto;

import java.time.LocalDateTime;

public class CompletedJourneyDto {
    private final RouteOptionDto route;
    private final String destination;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public CompletedJourneyDto(RouteOptionDto route, String destination, LocalDateTime startTime, LocalDateTime endTime) {
        this.route = route;
        this.destination = destination;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public RouteOptionDto getRoute() { return route; }
    public String getDestination() { return destination; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
