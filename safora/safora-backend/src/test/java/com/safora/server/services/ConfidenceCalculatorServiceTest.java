package com.safora.server.services;

import com.safora.server.config.SafetyWeightsConfig;
import com.safora.server.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfidenceCalculatorServiceTest {

    private ConfidenceCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        SafetyWeightsConfig config = new SafetyWeightsConfig();
        config.setLighting(0.3);
        config.setCommunityReports(0.4);
        config.setEmergencyZones(0.3);
        calculatorService = new ConfidenceCalculatorService(config);
    }

    @Test
    void testCalculateFinalConfidence_DayTime() {
        int score = calculatorService.calculateFinalConfidence(100, 100, 100, LocalTime.of(12, 0));
        assertEquals(100, score);
    }

    @Test
    void testCalculateFinalConfidence_NightTime() {
        // Lighting weight increases at night
        int score = calculatorService.calculateFinalConfidence(50, 100, 100, LocalTime.of(22, 0));
        assertEquals(79, score);
    }

    @Test
    void testDetermineRiskLevel() {
        assertEquals(RiskLevel.LOW, calculatorService.determineRiskLevel(85));
        assertEquals(RiskLevel.MEDIUM, calculatorService.determineRiskLevel(60));
        assertEquals(RiskLevel.HIGH, calculatorService.determineRiskLevel(30));
    }
}
