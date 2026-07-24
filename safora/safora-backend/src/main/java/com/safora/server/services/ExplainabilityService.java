package com.safora.server.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExplainabilityService {

    public List<String> generateExplanations(int lightScore, int reportScore, int emergencyScore) {
        List<String> explanations = new ArrayList<>();

        if (lightScore >= 80) explanations.add("✓ Well lit route");
        else if (lightScore < 50) explanations.add("⚠ Poorly lit areas");

        if (reportScore >= 80) explanations.add("✓ High community safety rating");
        else if (reportScore < 50) explanations.add("⚠ Multiple incident reports nearby");

        if (emergencyScore >= 80) explanations.add("✓ Close to police/hospital");

        if (explanations.isEmpty()) {
            explanations.add("Average safety conditions");
        }

        return explanations;
    }
}
