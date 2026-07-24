package com.safora.client.dto;

public class RouteCalculationRequest {
    private Double sourceLat;
    private Double sourceLng;
    private Double destLat;
    private Double destLng;
    private String travelMode = "WALKING";

    public RouteCalculationRequest() {}

    public Double getSourceLat() { return sourceLat; }
    public void setSourceLat(Double sourceLat) { this.sourceLat = sourceLat; }
    public Double getSourceLng() { return sourceLng; }
    public void setSourceLng(Double sourceLng) { this.sourceLng = sourceLng; }
    public Double getDestLat() { return destLat; }
    public void setDestLat(Double destLat) { this.destLat = destLat; }
    public Double getDestLng() { return destLng; }
    public void setDestLng(Double destLng) { this.destLng = destLng; }
    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
}
