package com.safora.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safora.client.components.MapViewComponent;
import com.safora.client.dto.CreateReportRequest;
import com.safora.client.dto.RouteOptionDto;
import com.safora.client.location.DesktopLocationProvider;
import com.safora.client.location.Location;
import com.safora.client.location.LocationProvider;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.JourneyManager;
import com.safora.client.services.ProfileService;
import com.safora.client.services.SafetyReportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.ArrayList;

public class LiveNavigationController {

    @FXML private MapViewComponent mapView;
    @FXML private Label navigationDestinationLabel;
    @FXML private Label timeLabel;
    @FXML private Label distanceLabel;
    @FXML private Label safetyLabel;
    @FXML private Label modeLabel;
    @FXML private Label progressBarLabel;
    @FXML private Button continueJourneyBtn;
    
    @FXML private VBox checkpointsListContainer;
    @FXML private VBox contextChipsContainer;

    @FXML private StackPane routeUpdateModal;

    @FXML private StackPane sosModal;
    @FXML private Label sosCountdownLabel;
    @FXML private StackPane shareModal;
    @FXML private VBox contactsListContainer;
    @FXML private Label shareLastUpdatedLabel;
    @FXML private StackPane reportModal;
    @FXML private ComboBox<String> incidentCategoryCombo;
    @FXML private ComboBox<String> incidentSeverityCombo;
    @FXML private TextField incidentDescField;
    @FXML private Label reportStatusLabel;

    private RouteOptionDto activeRoute;
    private final LocationProvider locationProvider = new DesktopLocationProvider();
    private final ObjectMapper mapper = new ObjectMapper();
    
    // Checkpoints data
    private Label cp1Icon, cp2Icon, cp3Icon, cp4Icon;
    private int currentProgress = 0;
    private int totalPoints = 0;
    
    // Presentation States
    private int presentationStep = 0; // 0=Start, 1=Police, 2=Lighting/Modal, 3=Destination

    @FXML
    public void initialize() {
        activeRoute = JourneyManager.getSelectedRoute();
        
        if (activeRoute != null) {
            String dest = JourneyManager.getDestination() != null ? JourneyManager.getDestination() : "Destination";
            navigationDestinationLabel.setText("Navigating to " + dest);
            
            timeLabel.setText(activeRoute.getEstimatedTimeMins() + " min");
            distanceLabel.setText(String.format("%.1f km", activeRoute.getDistanceKm()));
            safetyLabel.setText(activeRoute.getConfidenceScore() + "%");
            String mode = JourneyManager.getTransportMode();
            if (mode == null || mode.isEmpty()) mode = "Drive";
            modeLabel.setText(mode);
            
            setupCheckpointsPanel(dest);
            setupEvolvingChipsInitial();
            
            Platform.runLater(() -> {
                if (activeRoute.getPolyline() != null && !activeRoute.getPolyline().isEmpty()) {
                    try {
                        String json = mapper.writeValueAsString(activeRoute.getPolyline());
                        mapView.setCenter(activeRoute.getPolyline().get(0).get(0), activeRoute.getPolyline().get(0).get(1));
                        mapView.getEngine().executeScript("drawRoute('" + json + "');");
                    } catch (Exception e) {}
                }
            });
        }

        incidentCategoryCombo.getItems().addAll("Poor Lighting", "Harassment", "Road Accident", "Construction", "Medical Emergency", "Suspicious Activity");
        incidentSeverityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
        
        // Initial setup
        updateProgressUI(0, 100);
        cp1Icon.setText("✓");
    }
    
    private void setupCheckpointsPanel(String dest) {
        checkpointsListContainer.getChildren().clear();
        
        cp1Icon = new Label("●");
        cp2Icon = new Label("○");
        cp3Icon = new Label("○");
        cp4Icon = new Label("○");
        
        String start = JourneyManager.getSource() != null ? JourneyManager.getSource() : "Start Location";
        
        Label[] icons = {cp1Icon, cp2Icon, cp3Icon, cp4Icon};
        String[] titles = {start, "Police Checkpoint", "Well-Lit Zone", dest};
        
        for (int i = 0; i < 4; i++) {
            checkpointsListContainer.getChildren().add(createCheckpointRow(icons[i], titles[i]));
            if (i < 3) {
                javafx.scene.layout.VBox line = new javafx.scene.layout.VBox();
                line.setStyle("-fx-background-color: #E7DCCF; -fx-pref-width: 2px; -fx-pref-height: 15px;");
                javafx.scene.layout.VBox lineContainer = new javafx.scene.layout.VBox(line);
                lineContainer.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
                checkpointsListContainer.getChildren().add(lineContainer);
            }
        }
    }
    
    private javafx.scene.layout.HBox createCheckpointRow(Label icon, String text) {
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(15);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        icon.setStyle("-fx-font-size: 16px; -fx-text-fill: #B08968;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C2C2C; -fx-font-weight: bold;");
        textLabel.setWrapText(true);
        row.getChildren().addAll(icon, textLabel);
        return row;
    }
    
    private void setupEvolvingChipsInitial() {
        contextChipsContainer.getChildren().clear();
        addChip("🟢", "Safe Route Active", "Monitoring live conditions\n✓ Better lighting detected\n✓ Community reports monitored\n✓ Police checkpoint nearby\nLast checked: Just now");
    }
    
    private void addChip(String iconStr, String title, String subtitle) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(2);
        card.setStyle("-fx-background-color: #FFFDF8; -fx-padding: 10 15; -fx-background-radius: 8; -fx-border-color: #EAEAEA; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 2, 0, 0, 1);");
        
        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 14px;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2C2C2C;");
        header.getChildren().addAll(icon, titleLbl);
        
        Label subLbl = new Label(subtitle);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-padding: 0 0 0 22;");
        
        card.getChildren().addAll(header, subLbl);
        
        contextChipsContainer.getChildren().add(0, card);
        if (contextChipsContainer.getChildren().size() > 4) {
            contextChipsContainer.getChildren().remove(4, contextChipsContainer.getChildren().size());
        }
    }
    
    private void updateProgressUI(int index, int total) {
        int totalCheckpoints = 4;
        int completedCheckpoints = presentationStep;
        double pct = (double) completedCheckpoints / (totalCheckpoints - 1);
        
        int pctInt = (int)(pct * 100);
        int blocks = (int)(pct * 16);
        StringBuilder pb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i < blocks) pb.append("█");
            else pb.append("░");
        }
        progressBarLabel.setText(pb.toString() + "\n" + pctInt + "% Complete");
        
        int remainingMins = Math.max(0, (int)(activeRoute.getEstimatedTimeMins() * (1 - pct)));
        double remainingKm = Math.max(0.0, activeRoute.getDistanceKm() * (1 - pct));
        
        timeLabel.setText(remainingMins + " min");
        distanceLabel.setText(String.format("%.1f km", remainingKm));
        
        if (completedCheckpoints == 0) {
            cp1Icon.setText("●"); cp2Icon.setText("○"); cp3Icon.setText("○"); cp4Icon.setText("○");
        } else if (completedCheckpoints == 1) {
            cp1Icon.setText("✓"); cp2Icon.setText("●"); cp3Icon.setText("○"); cp4Icon.setText("○");
        } else if (completedCheckpoints == 2) {
            cp1Icon.setText("✓"); cp2Icon.setText("✓"); cp3Icon.setText("●"); cp4Icon.setText("○");
        } else {
            cp1Icon.setText("✓"); cp2Icon.setText("✓"); cp3Icon.setText("✓"); cp4Icon.setText("✓");
        }
    }
    
    @FXML
    public void handleContinueJourney() {
        if (activeRoute == null || activeRoute.getPolyline() == null) return;
        totalPoints = activeRoute.getPolyline().size();
        
        presentationStep++;
        
        int targetIndex = 0;
        
        if (presentationStep == 1) {
            // Move to Police Checkpoint
            targetIndex = (int)(totalPoints * 0.35);
            updateProgressUI(targetIndex, totalPoints);
            jumpToPoint(targetIndex);
            
            addChip("✓", "Police Checkpoint Passed", "Verified checkpoint");
            
        } else if (presentationStep == 2) {
            // Move to Well-Lit Zone & Trigger Modal
            targetIndex = (int)(totalPoints * 0.65);
            updateProgressUI(targetIndex, totalPoints);
            jumpToPoint(targetIndex);
            
            Platform.runLater(() -> routeUpdateModal.setVisible(true));
            
        } else if (presentationStep == 3) {
            // Move to Destination
            targetIndex = totalPoints - 1;
            updateProgressUI(targetIndex, totalPoints);
            jumpToPoint(targetIndex);
            
            continueJourneyBtn.setText("Complete Journey");
            
            final int finalTargetIndex = targetIndex;
            Platform.runLater(() -> {
                try {
                    String destName = JourneyManager.getDestination() != null ? JourneyManager.getDestination() : "Destination Reached";
                    List<Double> pEnd = activeRoute.getPolyline().get(finalTargetIndex);
                    mapView.getEngine().executeScript(String.format("addCheckpointMarker(%f, %f, 'destination', '📍 Destination Reached', '%s');", pEnd.get(0), pEnd.get(1), destName.replace("'", "\\'")));
                } catch (Exception e) {}
            });
            
        } else {
            // Finish
            handleEndJourney();
        }
    }
    
    private void jumpToPoint(int index) {
        if (index >= 0 && index < totalPoints) {
            List<Double> point = activeRoute.getPolyline().get(index);
            Platform.runLater(() -> {
                mapView.setCurrentLocation(point.get(0), point.get(1));
                mapView.setCenter(point.get(0), point.get(1));
            });
        }
    }

    @FXML
    public void handleStayOnRoute() {
        routeUpdateModal.setVisible(false);
    }
    
    @FXML
    public void handleSwitchRoute() {
        routeUpdateModal.setVisible(false);
        addChip("🟠", "Route Updated", "Community report detected\nAlternative route applied\nSafety improved");
        
        Platform.runLater(() -> {
            safetyLabel.setText("91%");
            try {
                // Mock a small deviation polyline switch
                mapView.getEngine().executeScript("clearMarkers();");
                String json = mapper.writeValueAsString(activeRoute.getPolyline());
                mapView.getEngine().executeScript("drawRoute('" + json + "', '#10B981');");
            } catch(Exception e) {}
        });
    }

    private Thread sosThread;

    @FXML
    public void handleSOS() {
        sosModal.setVisible(true);
        sosCountdownLabel.setText("60");
        if (sosThread != null) sosThread.interrupt();
        
        sosThread = new Thread(() -> {
            try {
                for (int i = 59; i >= 0; i--) {
                    Thread.sleep(1000);
                    final int count = i;
                    Platform.runLater(() -> {
                        if (sosModal.isVisible()) {
                            sosCountdownLabel.setText(String.valueOf(count));
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        sosThread.setDaemon(true);
        sosThread.start();
    }

    @FXML
    public void handleShareLocation() {
        contactsListContainer.getChildren().clear();
        ProfileService.getProfile().thenAccept(profile -> Platform.runLater(() -> {
            if (profile != null && profile.getEmergencyContacts() != null && !profile.getEmergencyContacts().isEmpty()) {
                for (var contact : profile.getEmergencyContacts()) {
                    Label contactLbl = new Label("✓ " + contact.getName());
                    contactLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C2C2C; -fx-font-weight: bold;");
                    contactsListContainer.getChildren().add(contactLbl);
                }
            } else {
                contactsListContainer.getChildren().add(new Label("✓ No contacts found"));
            }
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            shareLastUpdatedLabel.setText(java.time.LocalDateTime.now().format(dtf));
            shareModal.setVisible(true);
        }));
    }

    @FXML
    public void handleReportIncident() {
        incidentCategoryCombo.getSelectionModel().clearSelection();
        incidentSeverityCombo.getSelectionModel().clearSelection();
        incidentDescField.clear();
        reportStatusLabel.setVisible(false);
        reportModal.setVisible(true);
    }

    @FXML
    public void handleSubmitReport() {
        String category = incidentCategoryCombo.getValue();
        String severity = incidentSeverityCombo.getValue();
        String desc = incidentDescField.getText();
        
        if (category == null || severity == null) {
            reportStatusLabel.setText("Please select category and severity.");
            reportStatusLabel.setStyle("-fx-text-fill: #C62828;");
            reportStatusLabel.setVisible(true);
            return;
        }
        
        reportStatusLabel.setText("Submitting...");
        reportStatusLabel.setStyle("-fx-text-fill: #C97A00;");
        reportStatusLabel.setVisible(true);
        
        Location loc = locationProvider.getCurrentLocation();
        CreateReportRequest req = new CreateReportRequest();
        req.setLatitude(loc.getLat());
        req.setLongitude(loc.getLng());
        req.setCategory(category);
        req.setSeverity(severity);
        req.setDescription(desc);
        
        SafetyReportService.createReport(req).thenAccept(res -> Platform.runLater(() -> {
            reportModal.setVisible(false);
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                reportStatusLabel.setText("Failed to submit.");
                reportStatusLabel.setStyle("-fx-text-fill: #C62828;");
            });
            return null;
        });
    }

    @FXML
    public void handleCloseModal() {
        sosModal.setVisible(false);
        shareModal.setVisible(false);
        reportModal.setVisible(false);
    }

    @FXML
    public void handleEndJourney() {
        JourneyManager.endJourney();
        NavigationManager.navigate(Screen.JOURNEY_SUMMARY);
    }
}
