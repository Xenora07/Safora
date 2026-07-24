package com.safora.server.services;

import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.repositories.MockSafetyDataRepository;
import org.springframework.stereotype.Service;

@Service
public class EmergencyZoneService {
    private final MockSafetyDataRepository repository;

    public EmergencyZoneService(MockSafetyDataRepository repository) {
        this.repository = repository;
    }

    public int evaluateProximityScore(RouteOptionDto route) {
        return repository.getEmergencyProximityForPath(route.getPolylineMock());
    }
}
