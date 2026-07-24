package com.safora.server.dtos;

import com.safora.server.enums.TravelMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class RouteCalculationRequest {
    @NotNull(message = "Source latitude is required")
    private Double sourceLat;
    @NotNull(message = "Source longitude is required")
    private Double sourceLng;
    @NotNull(message = "Destination latitude is required")
    private Double destLat;
    @NotNull(message = "Destination longitude is required")
    private Double destLng;
    
    private TravelMode travelMode = TravelMode.WALKING;
    
    private LocalTime timeOfDay = LocalTime.now();

    // Getters and Setters
    public Double getSourceLat() { return sourceLat; }
    public void setSourceLat(Double sourceLat) { this.sourceLat = sourceLat; }
    public Double getSourceLng() { return sourceLng; }
    public void setSourceLng(Double sourceLng) { this.sourceLng = sourceLng; }
    public Double getDestLat() { return destLat; }
    public void setDestLat(Double destLat) { this.destLat = destLat; }
    public Double getDestLng() { return destLng; }
    public void setDestLng(Double destLng) { this.destLng = destLng; }
    public TravelMode getTravelMode() { return travelMode; }
    public void setTravelMode(TravelMode travelMode) { this.travelMode = travelMode; }
    public LocalTime getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(LocalTime timeOfDay) { this.timeOfDay = timeOfDay; }
}
