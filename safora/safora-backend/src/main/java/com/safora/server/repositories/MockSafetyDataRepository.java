package com.safora.server.repositories;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * A mock repository that holds hardcoded safety context data.
 * This simulates a geospatial database query where we would check
 * for streetlights, police stations, and incident reports overlapping a polyline.
 */
@Repository
public class MockSafetyDataRepository {
    
    // Map of PolylineMock string to a raw score out of 100 for that metric
    private final Map<String, Integer> lightingScores = new HashMap<>();
    private final Map<String, Integer> incidentScores = new HashMap<>(); // 100 = safe, 0 = dangerous
    private final Map<String, Integer> emergencyProximity = new HashMap<>();

    public MockSafetyDataRepository() {
        // Path A is fast but poorly lit and has some incidents, few police stations
        lightingScores.put("Path A (Main Highway)", 40);
        incidentScores.put("Path A (Main Highway)", 50);
        emergencyProximity.put("Path A (Main Highway)", 30);

        // Path B is well lit, lots of community activity, very safe, near police
        lightingScores.put("Path B (Inner City Streets)", 90);
        incidentScores.put("Path B (Inner City Streets)", 85);
        emergencyProximity.put("Path B (Inner City Streets)", 95);

        // Path C is average
        lightingScores.put("Path C (Bypass)", 65);
        incidentScores.put("Path C (Bypass)", 70);
        emergencyProximity.put("Path C (Bypass)", 50);
    }

    public int getLightingScoreForPath(String path) {
        return lightingScores.getOrDefault(path, 50);
    }

    public int getIncidentSafetyScoreForPath(String path) {
        return incidentScores.getOrDefault(path, 50);
    }

    public int getEmergencyProximityForPath(String path) {
        return emergencyProximity.getOrDefault(path, 50);
    }
}
