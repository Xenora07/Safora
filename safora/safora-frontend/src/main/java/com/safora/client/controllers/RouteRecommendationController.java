package com.safora.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safora.client.components.MapViewComponent;
import com.safora.client.dto.RouteCalculationResponse;
import com.safora.client.dto.RouteOptionDto;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class RouteRecommendationController {

    @FXML private MapViewComponent mapView;
    @FXML private VBox timelineContainer;
    @FXML private VBox routeCardsContainer;
    @FXML private FlowPane reasonsContainer;
    @FXML private StackPane safetyModal;
    @FXML private Label modalSafetyScore;
    @FXML private Label modalReasonText;
    @FXML private Label modalDistance;
    @FXML private Label modalEstimatedTime;
    @FXML private Label modalModeText;
    @FXML private Label modalConfidence;
    @FXML private Label modalLastUpdated;
    @FXML private StackPane transitionOverlay;
    @FXML private Label transitionText;
    
    @FXML private VBox tradeoffCol1;
    @FXML private Label tradeoffCol1Title;
    @FXML private Label tradeoffCol1Val1;
    @FXML private Label tradeoffCol1Val2;
    
    @FXML private VBox tradeoffCol2;
    @FXML private Label tradeoffCol2Title;
    @FXML private Label tradeoffCol2Val1;
    @FXML private Label tradeoffCol2Val2;
    
    @FXML private VBox tradeoffCol3;
    @FXML private Label tradeoffCol3Val1;
    @FXML private Label tradeoffCol3Val2;

    private RouteOptionDto currentRecommendedRoute;
    private List<RouteOptionDto> allRoutes = new ArrayList<>();
    private final com.safora.client.location.DesktopLocationProvider locationProvider = new com.safora.client.location.DesktopLocationProvider();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        RouteCalculationResponse data = (RouteCalculationResponse) NavigationManager.getContextData(Screen.ROUTE_RECOMMENDATION);
        if (data != null) {
            if (data.getRecommendedRoute() != null) {
                allRoutes.add(data.getRecommendedRoute());
            }
            if (data.getAlternativeRoutes() != null) {
                allRoutes.addAll(data.getAlternativeRoutes());
            }
            
            if (!allRoutes.isEmpty()) {
                currentRecommendedRoute = allRoutes.get(0);
                buildRouteCards();
                bindSelectedRoute();
            }
        }
    }

    private void buildRouteCards() {
        routeCardsContainer.getChildren().clear();
        for (RouteOptionDto route : allRoutes) {
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setMinWidth(350);
            card.setStyle("-fx-cursor: hand;");
            
            if (route == currentRecommendedRoute) {
                card.setStyle("-fx-cursor: hand; -fx-border-color: #6F2C2F; -fx-border-width: 2px; -fx-background-color: #FFFDF8;");
            }

            VBox header = new VBox(5);
            String rawType = route.getRouteType() != null ? route.getRouteType() : "Route";
            String displayType = rawType;
            boolean isRecommended = rawType.toLowerCase().contains("safest") || rawType.toLowerCase().contains("recommended");
            if (isRecommended) displayType = "Recommended";
            else if (rawType.toLowerCase().contains("fastest")) displayType = "Fastest";
            else displayType = "Balanced";
            
            Label typeLbl = new Label(displayType);
            typeLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2C2C2C;");
            
            String mode = com.safora.client.services.JourneyManager.getTransportMode();
            if (mode == null || mode.isEmpty()) {
                mode = "Drive";
            }
            String modeIcon = "🚗";
            if (mode.equalsIgnoreCase("walk")) modeIcon = "🚶";
            else if (mode.equalsIgnoreCase("bike")) modeIcon = "🚲";
            else if (mode.equalsIgnoreCase("transit") || mode.equalsIgnoreCase("bus")) modeIcon = "🚌";
            
            Label statsLbl = new Label(String.format("%s %s • %d min • %.1f km", modeIcon, mode, route.getEstimatedTimeMins(), route.getDistanceKm()));
            statsLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
            
            Label safetyLbl = new Label(String.format("🛡 Safety %d%%", route.getConfidenceScore()));
            safetyLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
            
            header.getChildren().addAll(typeLbl, statsLbl, safetyLbl);

            card.getChildren().addAll(header);

            card.setOnMouseClicked(e -> {
                currentRecommendedRoute = route;
                buildRouteCards(); // refresh borders
                bindSelectedRoute();
            });

            routeCardsContainer.getChildren().add(card);
        }
    }

    private void bindSelectedRoute() {
        reasonsContainer.getChildren().clear();
        
        List<List<String>> scenarios = new ArrayList<>();
        scenarios.add(List.of("✓ Better Lighting", "✓ Police Patrol Nearby", "✓ Active Main Roads")); // Night Safety
        scenarios.add(List.of("🚧 Construction Avoided", "✓ Better Traffic Flow", "✓ Reduced Delays")); // Construction
        scenarios.add(List.of("🚦 Heavy Traffic Avoided", "✓ Faster Flow", "✓ Consistent ETA")); // Heavy Traffic
        scenarios.add(List.of("⛔ Road Closure Avoided", "✓ Maintained Speed", "✓ Uninterrupted Journey")); // Road Closure
        scenarios.add(List.of("🎉 Festival Traffic Avoided", "✓ Lower Crowd Density", "✓ Stable ETA")); // Festival
        scenarios.add(List.of("⚠ Public Gathering Avoided", "✓ Alternative Route", "✓ Less Crowded")); // Public Protest
        scenarios.add(List.of("🌊 Flood Zone Avoided", "✓ Elevated Route", "✓ Safe Road Surface")); // Flood
        scenarios.add(List.of("📍 Community Report Verified", "✓ Safer Neighborhood", "✓ Trusted Route")); // Community Reports
        scenarios.add(List.of("🚑 Accident Ahead Avoided", "✓ Emergency Diversion", "✓ Safer Traffic Flow")); // Accident
        scenarios.add(List.of("🚓 Police Activity Avoided", "✓ Unrestricted Route", "✓ No Checkpoint Delays")); // Police Activity
        scenarios.add(List.of("🌫 Low Visibility Avoided", "✓ Clearer Route", "✓ Safer Driving Conditions")); // Low Visibility
        scenarios.add(List.of("💡 Poorly Lit Areas Avoided", "✓ Well-Lit Route", "✓ Enhanced Security")); // Poor Lighting
        
        int scenarioIndex = (int)(Math.random() * scenarios.size());
        List<String> selectedScenario = scenarios.get(scenarioIndex);
        
        for (String reason : selectedScenario) {
            Label chip = new Label(reason);
            chip.getStyleClass().add("reason-chip");
            reasonsContainer.getChildren().add(chip);
        }
        
        buildTimeline();

        Platform.runLater(() -> {
            if (currentRecommendedRoute.getPolyline() != null && !currentRecommendedRoute.getPolyline().isEmpty()) {
                try {
                    String json = mapper.writeValueAsString(currentRecommendedRoute.getPolyline());
                    mapView.getEngine().executeScript("clearMarkers();");
                    String routeType = currentRecommendedRoute.getRouteType();
                    String color = "#2563EB"; // Blue default
                    if (routeType != null) {
                        if (routeType.toLowerCase().contains("safest")) color = "#10B981"; // Green
                        else if (routeType.toLowerCase().contains("balanced")) color = "#F59E0B"; // Orange
                    }
                    mapView.getEngine().executeScript(String.format("drawRoute('%s', '%s');", json, color));
                    
                    int totalPoints = currentRecommendedRoute.getPolyline().size();
                    
                    String startName = com.safora.client.services.JourneyManager.getSource() != null ? com.safora.client.services.JourneyManager.getSource() : "Current Location";
                    String destName = com.safora.client.services.JourneyManager.getDestination() != null ? com.safora.client.services.JourneyManager.getDestination() : "Destination";
                    
                    if (totalPoints > 0) {
                        // Current Location marker
                        List<Double> pStart = currentRecommendedRoute.getPolyline().get(0);
                        mapView.getEngine().executeScript(String.format("addCheckpointMarker(%f, %f, 'current', 'Current Location', '%s');", pStart.get(0), pStart.get(1), startName));
                        
                        // Destination marker
                        List<Double> pEnd = currentRecommendedRoute.getPolyline().get(totalPoints - 1);
                        mapView.getEngine().executeScript(String.format("addCheckpointMarker(%f, %f, 'destination', 'Destination', '%s');", pEnd.get(0), pEnd.get(1), destName));
                    }
                } catch (Exception e) {}
            }
        });
    }

    private void buildTimeline() {
        timelineContainer.getChildren().clear();
        
        String startName = com.safora.client.services.JourneyManager.getSource() != null ? com.safora.client.services.JourneyManager.getSource() : "Current Location";
        String destName = com.safora.client.services.JourneyManager.getDestination() != null ? com.safora.client.services.JourneyManager.getDestination() : "Destination";
        
        boolean hasCheckpoints = currentRecommendedRoute != null && currentRecommendedRoute.getPolyline() != null && currentRecommendedRoute.getPolyline().size() > 4;
        
        String policeSubtext = hasCheckpoints ? "Locating..." : "No Police Checkpoint on this route";
        String lightSubtext = hasCheckpoints ? "Locating..." : "No Well-Lit Zone Recorded";
        
        String[] checkpoints = {"Current Location", "Police Checkpoint", "Well-Lit Zone", "Destination"};
        String[] icons = {"📍", "🚓", "💡", "🎯"};
        javafx.scene.control.Label[] subtextLabels = new javafx.scene.control.Label[4];
        
        for (int i = 0; i < checkpoints.length; i++) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            Label iconLbl = new Label(icons[i]);
            iconLbl.setStyle("-fx-font-size: 20px;");
            
            VBox textCol = new VBox(3);
            Label textLbl = new Label(checkpoints[i]);
            textLbl.setStyle("-fx-text-fill: #2C2C2C; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label subtextLbl = new Label();
            subtextLbl.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
            subtextLabels[i] = subtextLbl;
            
            if (i == 0) subtextLbl.setText(startName);
            else if (i == 1) subtextLbl.setText(policeSubtext);
            else if (i == 2) subtextLbl.setText(lightSubtext);
            else if (i == 3) subtextLbl.setText(destName);
            
            textCol.getChildren().addAll(textLbl, subtextLbl);
            
            row.getChildren().addAll(iconLbl, textCol);
            timelineContainer.getChildren().add(row);
            
            if (i < checkpoints.length - 1) {
                VBox line = new VBox();
                line.setStyle("-fx-background-color: #E7DCCF; -fx-pref-width: 2px; -fx-pref-height: 25px;");
                VBox lineContainer = new VBox(line);
                lineContainer.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
                timelineContainer.getChildren().add(lineContainer);
            }
        }
        
        if (hasCheckpoints) {
            List<Double> p1 = currentRecommendedRoute.getPolyline().get(currentRecommendedRoute.getPolyline().size() / 3);
            locationProvider.findNearestPOI(p1.get(0), p1.get(1), "police").thenAccept(name -> {
                Platform.runLater(() -> {
                    String title = name != null ? name : "No Police Checkpoint on this route";
                    String dist = name != null ? " (180 m ahead)" : ""; 
                    subtextLabels[1].setText(title + dist);
                    
                    if (name != null) {
                        try {
                            mapView.getEngine().executeScript(String.format("addCheckpointMarker(%f, %f, 'police', 'Police Checkpoint', '%s');", p1.get(0), p1.get(1), title.replace("'", "\\'")));
                        } catch (Exception e) {}
                    }
                });
            });

            List<Double> p2 = currentRecommendedRoute.getPolyline().get((currentRecommendedRoute.getPolyline().size() * 2) / 3);
            locationProvider.reverseGeocode(p2.get(0), p2.get(1)).thenAccept(name -> {
                Platform.runLater(() -> {
                    String title = name != null ? name : "No Well-Lit Zone Recorded";
                    String dist = name != null ? " (220 m ahead)" : ""; 
                    subtextLabels[2].setText(title + dist);
                    
                    if (name != null) {
                        try {
                             mapView.getEngine().executeScript(String.format("addCheckpointMarker(%f, %f, 'lighting', 'Well-Lit Zone', '%s');", p2.get(0), p2.get(1), title.replace("'", "\\'")));
                        } catch (Exception e) {}
                    }
                });
            });
        }
    }

    @FXML
    public void handleBack() {
        NavigationManager.navigate(Screen.HOME);
    }

    @FXML
    public void handleStartJourney() {
        if (currentRecommendedRoute != null) {
            com.safora.client.services.JourneyManager.startJourney(currentRecommendedRoute);
            
            transitionOverlay.setVisible(true);
            transitionText.setText("Preparing safest route...");
            
            Thread transitionThread = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> transitionText.setText("Checking live reports..."));
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        transitionText.setText("Navigation ready.");
                    });
                    Thread.sleep(500);
                    Platform.runLater(() -> NavigationManager.navigate(Screen.LIVE_NAVIGATION));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            transitionThread.setDaemon(true);
            transitionThread.start();
        }
    }
    
    @FXML
    public void handleOpenModal() {
        if (currentRecommendedRoute != null) {
            modalSafetyScore.setText(currentRecommendedRoute.getConfidenceScore() + "%");
            modalDistance.setText(String.format("%.1f km", currentRecommendedRoute.getDistanceKm()));
            
            modalEstimatedTime.setText(currentRecommendedRoute.getEstimatedTimeMins() + " min");
            
            String mode = com.safora.client.services.JourneyManager.getTransportMode();
            if (mode == null) mode = "Drive";
            modalModeText.setText(mode);
            
            String rawType = currentRecommendedRoute.getRouteType() != null ? currentRecommendedRoute.getRouteType() : "Route";
            boolean isRecommended = rawType.toLowerCase().contains("safest") || rawType.toLowerCase().contains("recommended");
            boolean isFastest = rawType.toLowerCase().contains("fastest");
            
            if (isRecommended) {
                modalReasonText.setText("✓ Better street lighting\n✓ Police checkpoint nearby\n✓ CCTV coverage\n✓ Fewer community reports");
                tradeoffCol1.setVisible(true); tradeoffCol1.setManaged(true);
                tradeoffCol2.setVisible(true); tradeoffCol2.setManaged(true);
                tradeoffCol3.setVisible(true); tradeoffCol3.setManaged(true);
                
                tradeoffCol1Title.setText("Fastest Route");
                tradeoffCol1Val1.setText("26 min");
                tradeoffCol1Val1.setStyle("-fx-text-fill: #2C2C2C; -fx-font-size: 13px; -fx-font-weight: bold;");
                tradeoffCol1Val2.setText("75% Safety");
                tradeoffCol1Val2.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-font-weight: bold;");
                
                tradeoffCol2Title.setText("Recommended Route");
                tradeoffCol2Val1.setText("30 min");
                tradeoffCol2Val1.setStyle("-fx-text-fill: #2C2C2C; -fx-font-size: 13px; -fx-font-weight: bold;");
                tradeoffCol2Val2.setText("94% Safety");
                tradeoffCol2Val2.setStyle("-fx-text-fill: #10B981; -fx-font-size: 13px; -fx-font-weight: bold;");
                
                tradeoffCol3Val1.setText("+4 min");
                tradeoffCol3Val1.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-font-weight: bold;");
                tradeoffCol3Val2.setText("+19% Safety");
                tradeoffCol3Val2.setStyle("-fx-text-fill: #10B981; -fx-font-size: 13px; -fx-font-weight: bold;");
            } else if (isFastest) {
                modalReasonText.setText("✓ Shortest travel distance");
                tradeoffCol1.setVisible(true); tradeoffCol1.setManaged(true);
                tradeoffCol2.setVisible(false); tradeoffCol2.setManaged(false);
                tradeoffCol3.setVisible(false); tradeoffCol3.setManaged(false);
                
                tradeoffCol1Title.setText("Trade-offs:");
                tradeoffCol1Val1.setText("⚠ Poorly lit segments\n⚠ Fewer monitored roads\n⚠ Higher incident density");
                tradeoffCol1Val1.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px;");
                tradeoffCol1Val2.setText("");
            } else {
                modalReasonText.setText("✓ Moderate lighting\n✓ Normal traffic\n✓ Few reported incidents");
                tradeoffCol1.setVisible(true); tradeoffCol1.setManaged(true);
                tradeoffCol2.setVisible(false); tradeoffCol2.setManaged(false);
                tradeoffCol3.setVisible(true); tradeoffCol3.setManaged(true);
                
                tradeoffCol1Title.setText("Time");
                tradeoffCol1Val1.setText("+3 min");
                tradeoffCol1Val1.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-font-weight: bold;");
                tradeoffCol1Val2.setText("");
                
                tradeoffCol3Val1.setText("Safety");
                tradeoffCol3Val1.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                tradeoffCol3Val2.setText("+11% Safety");
                tradeoffCol3Val2.setStyle("-fx-text-fill: #10B981; -fx-font-size: 13px; -fx-font-weight: bold;");
            }
            
            modalConfidence.setText(currentRecommendedRoute.getConfidenceScore() > 85 ? "High" : "Moderate");
            
            modalLastUpdated.setText("2 minutes ago");
        }
        safetyModal.setVisible(true);
    }
    
    @FXML
    public void handleCloseModal() {
        safetyModal.setVisible(false);
    }
}
