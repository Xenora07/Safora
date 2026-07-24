package com.safora.client.controllers;

import com.safora.client.dto.CreateReportRequest;
import com.safora.client.location.Location;
import com.safora.client.location.LocationProvider;
import com.safora.client.location.MockLocationProvider;
import com.safora.client.services.CommunityReportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReportIncidentDialogController {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> severityCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Label errorLabel;

    private Stage dialogStage;
    private CommunityReportsController parentController;
    private final LocationProvider locationProvider = new MockLocationProvider();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setParentController(CommunityReportsController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

    @FXML
    public void handleSubmit() {
        String category = categoryCombo.getValue();
        String severity = severityCombo.getValue();
        String desc = descriptionArea.getText();
        
        if (category == null || severity == null) {
            errorLabel.setText("Please select category and severity.");
            errorLabel.setVisible(true);
            return;
        }
        
        errorLabel.setVisible(false);
        
        Location loc = locationProvider.getCurrentLocation();
        
        CreateReportRequest req = new CreateReportRequest();
        req.setLatitude(loc.getLat());
        req.setLongitude(loc.getLng());
        req.setCategory(category);
        req.setSeverity(severity);
        req.setDescription(desc);
        
        CommunityReportService.submitReport(req)
            .thenAccept(res -> Platform.runLater(() -> {
                if (parentController != null) {
                    parentController.loadNearbyReports();
                }
                dialogStage.close();
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("Exception: ")) {
                        msg = msg.substring(msg.indexOf("Exception: ") + 11);
                    } else if (msg != null && msg.contains("RuntimeException: ")) {
                        msg = msg.substring(msg.indexOf("RuntimeException: ") + 18);
                    } else if (msg == null || msg.isEmpty()) {
                        msg = "Failed to submit report.";
                    }
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                });
                return null;
            });
    }
}
