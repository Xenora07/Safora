package com.safora.server.controllers;

import com.safora.server.dtos.RouteCalculationRequest;
import com.safora.server.dtos.RouteCalculationResponse;
import com.safora.server.dtos.RouteHistoryDto;
import com.safora.server.entities.RouteHistory;
import com.safora.server.repositories.RouteHistoryRepository;
import com.safora.server.services.RouteRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteRecommendationService recommendationService;
    private final RouteHistoryRepository historyRepository;

    public RouteController(RouteRecommendationService recommendationService, RouteHistoryRepository historyRepository) {
        this.recommendationService = recommendationService;
        this.historyRepository = historyRepository;
    }

    /**
     * Accepts source, destination, mode, context, etc., and returns evaluated routes.
     */
    @PostMapping("/calculate")
    public ResponseEntity<RouteCalculationResponse> calculateRoutes(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @Valid @RequestBody RouteCalculationRequest request) {
        
        RouteCalculationResponse response = recommendationService.calculateBestRoutes(request);
        
        // Save to history
        RouteHistory history = new RouteHistory();
        history.setUserId(userId);
        history.setSourceLat(request.getSourceLat());
        history.setSourceLng(request.getSourceLng());
        history.setDestLat(request.getDestLat());
        history.setDestLng(request.getDestLng());
        history.setTravelMode(request.getTravelMode());
        history.setRecommendedRouteId(response.getRecommendedRoute().getRouteId());
        history.setFinalConfidenceScore(response.getRecommendedRoute().getConfidenceScore());
        
        historyRepository.save(history);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RouteHistoryDto>> getHistory(@RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        List<RouteHistoryDto> dtos = historyRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteHistoryDto> getRouteById(@PathVariable Long id) {
        return historyRepository.findById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    private RouteHistoryDto mapToDto(RouteHistory history) {
        RouteHistoryDto dto = new RouteHistoryDto();
        dto.setId(history.getId());
        dto.setSourceLat(history.getSourceLat());
        dto.setSourceLng(history.getSourceLng());
        dto.setDestLat(history.getDestLat());
        dto.setDestLng(history.getDestLng());
        dto.setTravelMode(history.getTravelMode());
        dto.setRecommendedRouteId(history.getRecommendedRouteId());
        dto.setFinalConfidenceScore(history.getFinalConfidenceScore());
        dto.setCreatedAt(history.getCreatedAt());
        return dto;
    }
}
