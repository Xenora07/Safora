package com.safora.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safora.client.components.MapViewComponent;
import com.safora.client.dto.CompletedJourneyDto;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.JourneyManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;

public class JourneySummaryController {

    @FXML private MapViewComponent mapView;
    @FXML private Label destinationLabel;
    @FXML private Label timeLabel;
    @FXML private Label distanceLabel;
    @FXML private Label confidenceLabel;
    @FXML private Label startedLabel;
    @FXML private Label endedLabel;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        CompletedJourneyDto journey = JourneyManager.getCompletedJourney();
        
        if (journey != null && journey.getRoute() != null) {
            destinationLabel.setText(journey.getDestination() != null ? journey.getDestination() : "Unknown Destination");
            timeLabel.setText(journey.getRoute().getEstimatedTimeMins() + " mins");
            distanceLabel.setText(String.format("%.1f km", journey.getRoute().getDistanceKm()));
            confidenceLabel.setText(journey.getRoute().getConfidenceScore() + "%");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            if (journey.getStartTime() != null) {
                startedLabel.setText(journey.getStartTime().format(formatter));
            }
            if (journey.getEndTime() != null) {
                endedLabel.setText(journey.getEndTime().format(formatter));
            }

            Platform.runLater(() -> {
                if (journey.getRoute().getPolyline() != null) {
                    try {
                        String json = mapper.writeValueAsString(journey.getRoute().getPolyline());
                        mapView.setCenter(journey.getRoute().getPolyline().get(0).get(0), journey.getRoute().getPolyline().get(0).get(1));
                        mapView.getEngine().executeScript("drawRoute('" + json + "');");
                    } catch (Exception e) {}
                }
            });
        }
    }

    @FXML
    public void handleReportIncident() {
        NavigationManager.navigate(Screen.COMMUNITY_REPORTS);
    }

    @FXML
    public void handleSkip() {
        NavigationManager.navigate(Screen.HOME);
    }

    @FXML
    public void handleReturnHome() {
        NavigationManager.navigate(Screen.HOME);
    }
}
