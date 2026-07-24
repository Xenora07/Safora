package com.safora.server.services;

import com.safora.server.dtos.RouteOptionDto;
import com.safora.server.repositories.MockSafetyDataRepository;
import org.springframework.stereotype.Service;

@Service
public class LightingService {
    private final MockSafetyDataRepository repository;

    public LightingService(MockSafetyDataRepository repository) {
        this.repository = repository;
    }

    public int evaluateLightingScore(RouteOptionDto route) {
        return repository.getLightingScoreForPath(route.getPolylineMock());
    }
}
