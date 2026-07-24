package com.safora.server.services;

import com.safora.server.config.SafetyWeightsConfig;
import com.safora.server.enums.RiskLevel;
import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
public class ConfidenceCalculatorService {
    private final SafetyWeightsConfig weights;

    public ConfidenceCalculatorService(SafetyWeightsConfig weights) {
        this.weights = weights;
    }

    public int calculateFinalConfidence(int lightScore, int reportScore, int emergencyScore, LocalTime timeOfDay) {
        // Adjust weights based on time of day
        double dynamicLightWeight = weights.getLighting();
        if (timeOfDay.isAfter(LocalTime.of(18, 0)) || timeOfDay.isBefore(LocalTime.of(6, 0))) {
            // Lighting is more important at night
            dynamicLightWeight += 0.2;
        }

        double totalScore = (lightScore * dynamicLightWeight) +
                            (reportScore * weights.getCommunityReports()) +
                            (emergencyScore * weights.getEmergencyZones());
        
        // Normalize
        double totalWeights = dynamicLightWeight + weights.getCommunityReports() + weights.getEmergencyZones();
        return (int) Math.round(totalScore / totalWeights);
    }

    public RiskLevel determineRiskLevel(int confidenceScore) {
        if (confidenceScore >= 80) return RiskLevel.LOW;
        if (confidenceScore >= 50) return RiskLevel.MEDIUM;
        return RiskLevel.HIGH;
    }
}
