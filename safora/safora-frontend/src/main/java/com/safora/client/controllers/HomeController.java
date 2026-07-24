package com.safora.client.controllers;

import com.safora.client.components.LoadingOverlay;
import com.safora.client.components.MapViewComponent;
import com.safora.client.dto.RouteCalculationRequest;
import com.safora.client.location.DesktopLocationProvider;
import com.safora.client.location.Location;
import com.safora.client.location.LocationProvider;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.AuthService;
import com.safora.client.services.RouteService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class HomeController {

    @FXML private StackPane rootPane;
    @FXML private TextField searchField;
    @FXML private TextField currentLocationField;
    @FXML private Label greetingLabel;
    @FXML private Label userNameLabel;
    @FXML private Label errorLabel;
    @FXML private MapViewComponent mapView;
    @FXML private javafx.scene.layout.VBox demoModeBadge;
    
    @FXML private ToggleButton modeDrive;
    @FXML private ToggleButton modeWalk;
    @FXML private ToggleButton modeBike;
    @FXML private ToggleButton modeTransit;

    private LoadingOverlay loadingOverlay;
    private final DesktopLocationProvider locationProvider = new DesktopLocationProvider();

    @FXML
    public void initialize() {
        loadingOverlay = new LoadingOverlay();
        rootPane.getChildren().add(loadingOverlay);

        if (AuthService.getCurrentUser() != null) {
            String fullName = AuthService.getCurrentUser().getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                userNameLabel.setText(fullName);
            } else {
                userNameLabel.setText("User");
            }
        }

        if (AuthService.isDemoMode()) {
            demoModeBadge.setVisible(true);
            demoModeBadge.setManaged(true);
        }
        
        // Setup Transport Mode Toggle Group
        ToggleGroup transportGroup = new ToggleGroup();
        modeDrive.setToggleGroup(transportGroup);
        modeWalk.setToggleGroup(transportGroup);
        modeBike.setToggleGroup(transportGroup);
        modeTransit.setToggleGroup(transportGroup);
        
        transportGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true); // Prevent deselecting all
            } else {
                ToggleButton selected = (ToggleButton) newVal;
                String text = selected.getText().replaceAll("[^a-zA-Z]", "").trim();
                com.safora.client.services.JourneyManager.setTransportMode(text);
            }
        });
        com.safora.client.services.JourneyManager.setTransportMode("Drive");

        // Wait a bit for map to initialize and set center
        Platform.runLater(() -> {
            Location loc = locationProvider.getCurrentLocation();
            if (loc != null) {
                mapView.setCenter(loc.getLat(), loc.getLng());
                mapView.setCurrentLocation(loc.getLat(), loc.getLng());
            }
        });
    }

    @FXML
    public void handleOpenProfile() {
        NavigationManager.navigate(Screen.PROFILE);
    }

    @FXML
    public void handleUseGPS() {
        Location loc = locationProvider.getCurrentLocation();
        if (loc != null) {
            currentLocationField.setText(String.format("%.4f, %.4f", loc.getLat(), loc.getLng()));
            mapView.setCenter(loc.getLat(), loc.getLng());
        }
    }

    @FXML
    public void handleSafePlacePolice() {
        searchField.setText("Police Station");
    }

    @FXML
    public void handleSafePlaceHospital() {
        searchField.setText("Hospital");
    }

    @FXML
    public void handleSafePlaceMetro() {
        searchField.setText("Metro Station");
    }

    @FXML
    public void handleAnalyzeJourney() {
        String destination = searchField.getText();
        String currentLocText = currentLocationField.getText();

        if (destination == null || destination.trim().isEmpty()) {
            errorLabel.setText("Please enter a destination.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        loadingOverlay.show();

        // Start rotating messages task
        javafx.concurrent.Task<Void> loadingTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                String[] msgs = {
                    "Preparing your journey...",
                    "Checking road conditions...",
                    "Analyzing community reports...",
                    "Evaluating route safety...",
                    "Finding the best recommendation..."
                };
                int idx = 0;
                while (!isCancelled()) {
                    updateMessage(msgs[idx % msgs.length]);
                    idx++;
                    Thread.sleep(700);
                }
                return null;
            }
        };
        loadingTask.messageProperty().addListener((obs, old, msg) -> loadingOverlay.setText(msg));
        Thread t = new Thread(loadingTask);
        t.setDaemon(true);
        t.start();

        com.safora.client.services.JourneyManager.setDestination(destination);
        String sourceName = (currentLocText != null && !currentLocText.trim().isEmpty()) ? currentLocText : "Current Location";
        if (sourceName.matches("-?\\d+\\.\\d+,\\s*-?\\d+\\.\\d+")) {
            sourceName = "GPS Coordinates";
        }
        com.safora.client.services.JourneyManager.setSource(sourceName);

        java.util.concurrent.CompletableFuture<Location> sourceFuture;
        if (currentLocText != null && !currentLocText.trim().isEmpty()) {
            if (currentLocText.matches("-?\\d+\\.\\d+,\\s*-?\\d+\\.\\d+")) {
                String[] parts = currentLocText.split(",");
                sourceFuture = java.util.concurrent.CompletableFuture.completedFuture(new Location(Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim())));
            } else {
                sourceFuture = locationProvider.resolveDestinationAsync(currentLocText);
            }
        } else {
            sourceFuture = java.util.concurrent.CompletableFuture.completedFuture(locationProvider.getCurrentLocation());
        }

        java.util.concurrent.CompletableFuture<Location> destFuture = locationProvider.resolveDestinationAsync(destination);

        java.util.concurrent.CompletableFuture.allOf(sourceFuture, destFuture)
            .orTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
            .thenAccept(v -> {
                Location source = sourceFuture.join();
                Location dest = destFuture.join();

                if (dest != null && source != null) {
                    Platform.runLater(() -> mapView.addMarker(dest.getLat(), dest.getLng(), "destination"));

                    RouteCalculationRequest request = new RouteCalculationRequest();
                    request.setSourceLat(source.getLat());
                    request.setSourceLng(source.getLng());
                    request.setDestLat(dest.getLat());
                    request.setDestLng(dest.getLng());

                    RouteService.calculateRoutes(request)
                        .orTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
                        .thenAccept(response -> Platform.runLater(() -> {
                            loadingTask.cancel();
                            loadingOverlay.hide();
                            NavigationManager.navigate(Screen.ROUTE_RECOMMENDATION, response);
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingTask.cancel();
                                loadingOverlay.hide();
                                if (ex instanceof java.util.concurrent.TimeoutException || ex.getCause() instanceof java.util.concurrent.TimeoutException) {
                                    errorLabel.setText("Unable to reach the server. Please try again.");
                                } else {
                                    errorLabel.setText("Unable to analyze your journey. Please try again.");
                                }
                                errorLabel.setVisible(true);
                            });
                            return null;
                        });
                } else {
                    Platform.runLater(() -> {
                        loadingTask.cancel();
                        loadingOverlay.hide();
                        errorLabel.setText("Failed to locate destination.");
                        errorLabel.setVisible(true);
                    });
                }
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    loadingTask.cancel();
                    loadingOverlay.hide();
                    if (ex instanceof java.util.concurrent.TimeoutException || ex.getCause() instanceof java.util.concurrent.TimeoutException) {
                        errorLabel.setText("Unable to reach the server. Please try again.");
                    } else {
                        errorLabel.setText("Unable to analyze your journey. Please try again.");
                    }
                    errorLabel.setVisible(true);
                });
                return null;
            });
    }
}
