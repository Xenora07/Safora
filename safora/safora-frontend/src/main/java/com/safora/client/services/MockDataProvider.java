package com.safora.client.services;

import com.safora.client.dto.EmergencyContactDto;
import com.safora.client.dto.RouteHistoryDto;
import com.safora.client.dto.UserProfileDto;

import java.util.ArrayList;
import java.util.List;

public class MockDataProvider {

    public static UserProfileDto getMockProfile() {
        UserProfileDto profile = new UserProfileDto();
        profile.setId(999L);
        profile.setFullName("Nidhi");
        profile.setEmail("demo@safora.com");
        profile.setPhone("+91 9876543210");
        
        List<EmergencyContactDto> contacts = new ArrayList<>();
        EmergencyContactDto mother = new EmergencyContactDto();
        mother.setId(1L);
        mother.setName("Mother");
        mother.setRelationship("Family");
        mother.setPhoneNumber("9876543210");
        contacts.add(mother);
        
        EmergencyContactDto friend = new EmergencyContactDto();
        friend.setId(2L);
        friend.setName("Friend");
        friend.setRelationship("Friend");
        friend.setPhoneNumber("9876543211");
        contacts.add(friend);
        
        profile.setEmergencyContacts(contacts);
        return profile;
    }
    
    public static RouteHistoryDto[] getMockJourneyHistory() {
        RouteHistoryDto h1 = new RouteHistoryDto();
        h1.setRecommendedRouteId("Safe_Route_A");
        h1.setTravelMode("WALKING");
        h1.setFinalConfidenceScore(94);
        
        RouteHistoryDto h2 = new RouteHistoryDto();
        h2.setRecommendedRouteId("Metro_Connect_B");
        h2.setTravelMode("DRIVING");
        h2.setFinalConfidenceScore(88);
        
        return new RouteHistoryDto[]{h1, h2};
    }

    public static com.safora.client.dto.RouteCalculationResponse getMockRouteRecommendation() {
        com.safora.client.dto.RouteCalculationResponse response = new com.safora.client.dto.RouteCalculationResponse();
        
        List<List<Double>> polylineSafe = new ArrayList<>();
        polylineSafe.add(java.util.Arrays.asList(19.0441, 72.9103)); // Shah and Anchor
        polylineSafe.add(java.util.Arrays.asList(19.0300, 72.8900));
        polylineSafe.add(java.util.Arrays.asList(19.0150, 72.8700));
        polylineSafe.add(java.util.Arrays.asList(19.0000, 72.8500));
        polylineSafe.add(java.util.Arrays.asList(18.9800, 72.8400));
        polylineSafe.add(java.util.Arrays.asList(18.9600, 72.8300));
        polylineSafe.add(java.util.Arrays.asList(18.9440, 72.8228)); // Marine Drive
        
        double calculatedDist = calculateDistance(polylineSafe);
        double distSafest = Math.round(calculatedDist * 10.0) / 10.0;
        
        String mode = com.safora.client.services.JourneyManager.getTransportMode();
        double speedKmh = 30.0;
        if ("Walk".equalsIgnoreCase(mode)) speedKmh = 5.0;
        else if ("Bike".equalsIgnoreCase(mode)) speedKmh = 15.0;
        else if ("Transit".equalsIgnoreCase(mode)) speedKmh = 25.0;
        
        int timeSafest = (int) Math.round((distSafest / speedKmh) * 60);

        com.safora.client.dto.RouteOptionDto safest = new com.safora.client.dto.RouteOptionDto();
        safest.setRouteType("Safest Route");
        safest.setEstimatedTimeMins(timeSafest);
        safest.setDistanceKm(distSafest);
        safest.setConfidenceScore(94);
        safest.setExplanations(java.util.Arrays.asList("Better Lighting", "Police Checkpoint Nearby", "CCTV Coverage"));
        safest.setPolyline(polylineSafe);
        
        com.safora.client.dto.RouteOptionDto balanced = new com.safora.client.dto.RouteOptionDto();
        balanced.setRouteType("Balanced Route");
        balanced.setEstimatedTimeMins((int)(timeSafest * 0.95)); // Slightly faster
        balanced.setDistanceKm(distSafest);
        balanced.setConfidenceScore(86);
        balanced.setExplanations(java.util.Arrays.asList("Moderate Traffic", "Street Lights"));
        balanced.setPolyline(polylineSafe);
        
        com.safora.client.dto.RouteOptionDto fastest = new com.safora.client.dto.RouteOptionDto();
        fastest.setRouteType("Fastest Route");
        fastest.setEstimatedTimeMins((int)(timeSafest * 0.85)); // 15% faster
        fastest.setDistanceKm(distSafest);
        fastest.setConfidenceScore(75);
        fastest.setExplanations(java.util.Arrays.asList("Busy Roads", "Few Incidents"));
        fastest.setPolyline(polylineSafe);

        response.setRecommendedRoute(safest);
        response.setAlternativeRoutes(java.util.Arrays.asList(balanced, fastest));
        return response;
    }
    
    private static double calculateDistance(List<List<Double>> polyline) {
        double dist = 0.0;
        for (int i = 0; i < polyline.size() - 1; i++) {
            dist += haversine(polyline.get(i).get(0), polyline.get(i).get(1), 
                              polyline.get(i+1).get(0), polyline.get(i+1).get(1));
        }
        return dist;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
