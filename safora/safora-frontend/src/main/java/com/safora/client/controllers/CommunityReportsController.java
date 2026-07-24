package com.safora.client.controllers;

import com.safora.client.components.LoadingOverlay;
import com.safora.client.components.MapViewComponent;
import com.safora.client.dto.NearbyReportResponse;
import com.safora.client.location.DesktopLocationProvider;
import com.safora.client.location.Location;
import com.safora.client.location.LocationProvider;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.CommunityReportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CommunityReportsController {

    @FXML private StackPane rootPane;
    @FXML private VBox reportsContainer;
    @FXML private MapViewComponent mapView;

    private LoadingOverlay loadingOverlay;
    private final LocationProvider locationProvider = new DesktopLocationProvider();

    @FXML
    public void initialize() {
        loadingOverlay = new LoadingOverlay();
        rootPane.getChildren().add(loadingOverlay);
        
        Platform.runLater(() -> {
            Location loc = locationProvider.getCurrentLocation();
            if (loc != null) {
                mapView.setCenter(loc.getLat(), loc.getLng());
                mapView.setCurrentLocation(loc.getLat(), loc.getLng());
            }
        });

        loadNearbyReports();
    }

    public void loadNearbyReports() {
        loadingOverlay.show();
        Location loc = locationProvider.getCurrentLocation();
        
        CommunityReportService.getNearbyReports(loc.getLat(), loc.getLng(), 10.0)
            .thenAccept(reports -> Platform.runLater(() -> {
                loadingOverlay.hide();
                populateReports(reports);
                
                // Add markers to map
                if (reports != null) {
                    for (NearbyReportResponse report : reports) {
                        String type = report.getSeverity() != null && report.getSeverity().equals("CRITICAL") ? "warning" : "warning";
                        if (report.getLatitude() != null && report.getLongitude() != null) {
                            mapView.addMarker(report.getLatitude(), report.getLongitude(), type);
                        }
                    }
                }
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    loadingOverlay.hide();
                    reportsContainer.getChildren().clear();
                    Label error = new Label("Failed to load reports");
                    error.setStyle("-fx-text-fill: #EF4444;");
                    reportsContainer.getChildren().add(error);
                });
                return null;
            });
    }

    private void populateReports(NearbyReportResponse[] reports) {
        reportsContainer.getChildren().clear();
        if (reports == null || reports.length == 0) {
            Label noReports = new Label("No reports submitted.");
            noReports.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            reportsContainer.getChildren().add(noReports);
            return;
        }

        for (NearbyReportResponse report : reports) {
            HBox card = new HBox(15);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 10px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: #E5E7EB; -fx-effect: none;");
            
            VBox info = new VBox(5);
            Label typeLbl = new Label(report.getCategory() != null ? report.getCategory().replace("_", " ") : "Unknown");
            typeLbl.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 14px;");
            
            String distanceStr = report.getDistanceKm() != null ? String.format("%.1f km away", report.getDistanceKm()) : "Nearby";
            Label detailsLbl = new Label(distanceStr);
            detailsLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
            
            info.getChildren().addAll(typeLbl, detailsLbl);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label severityBadge = new Label("•");
            severityBadge.setStyle("-fx-font-size: 24px; -fx-translate-y: -2; -fx-text-fill: " + getSeverityColor(report.getSeverity()) + ";");
            
            card.getChildren().addAll(info, spacer, severityBadge);
            reportsContainer.getChildren().add(card);
        }
    }
    
    private String getSeverityColor(String severity) {
        if (severity == null) return "#94A3B8";
        switch(severity) {
            case "LOW": return "#10B981";
            case "MEDIUM": return "#F59E0B";
            case "HIGH": return "#EF4444";
            case "CRITICAL": return "#991B1B";
            default: return "#94A3B8";
        }
    }

    @FXML
    public void handleOpenReportDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/safora/client/views/ReportIncidentDialog.fxml"));
            Parent root = loader.load();
            
            ReportIncidentDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            controller.setParentController(this);
            
            dialogStage.showAndWait();
        } catch (IOException e) {
            // Ignored for presentation
        }
    }

    @FXML
    public void handleBack() {
        NavigationManager.navigate(Screen.HOME);
    }
}
