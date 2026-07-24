package com.safora.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "safora.safety.weights")
public class SafetyWeightsConfig {
    private double lighting = 0.3;
    private double communityReports = 0.4;
    private double emergencyZones = 0.3;

    // Getters and Setters
    public double getLighting() { return lighting; }
    public void setLighting(double lighting) { this.lighting = lighting; }
    public double getCommunityReports() { return communityReports; }
    public void setCommunityReports(double communityReports) { this.communityReports = communityReports; }
    public double getEmergencyZones() { return emergencyZones; }
    public void setEmergencyZones(double emergencyZones) { this.emergencyZones = emergencyZones; }
}
