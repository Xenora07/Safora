package com.safora.client.controllers;

import com.safora.client.components.LoadingOverlay;
import com.safora.client.dto.EmergencyContactDto;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.ProfileService;
import com.safora.client.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProfileController {

    @FXML private StackPane rootPane;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label prefModeLabel;

    @FXML private VBox emergencyContactsContainer;
    @FXML private VBox historyContainer;

    @FXML private StackPane contactModalOverlay;
    @FXML private Label modalTitleLabel;
    @FXML private TextField contactNameField;
    @FXML private TextField contactPhoneField;
    @FXML private Label modalErrorLabel;
    @FXML private Button modalSaveButton;

    private LoadingOverlay loadingOverlay;
    private EmergencyContactDto currentEditingContact = null;

    @FXML
    public void initialize() {
        loadingOverlay = new LoadingOverlay();
        rootPane.getChildren().add(loadingOverlay);
        loadProfile();
    }

    private void loadProfile() {
        loadingOverlay.show();
        
        ProfileService.getProfile()
            .thenAccept(profile -> Platform.runLater(() -> {
                if (profile != null) {
                    nameLabel.setText(profile.getFullName());
                    emailLabel.setText(profile.getEmail());
                    
                    String mode = com.safora.client.services.JourneyManager.getTransportMode();
                    if (mode == null || mode.isEmpty()) mode = "Drive";
                    if (prefModeLabel != null) {
                        prefModeLabel.setText("Preferred Mode: " + mode);
                    }
                    
                    emergencyContactsContainer.getChildren().clear();
                    if (profile.getEmergencyContacts() != null && !profile.getEmergencyContacts().isEmpty()) {
                        for (var contact : profile.getEmergencyContacts()) {
                            Label nameLbl = new Label(contact.getName());
                            nameLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #2B2B2B; -fx-font-weight: bold;");
                            
                            Label detailsLbl = new Label(contact.getRelationship() + " • " + contact.getPhoneNumber());
                            detailsLbl.getStyleClass().add("subtitle");
                            detailsLbl.setStyle("-fx-font-size: 13px;");
                            
                            VBox infoBox = new VBox(5, nameLbl, detailsLbl);
                            
                            Region spacer = new Region();
                            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                            
                            Button editBtn = new Button("Edit");
                            editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #A47551; -fx-cursor: hand;");
                            editBtn.setOnAction(e -> handleShowEditContactModal(contact));
                            
                            Button deleteBtn = new Button("Delete");
                            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #C62828; -fx-cursor: hand;");
                            deleteBtn.setOnAction(e -> handleDeleteContact(contact.getId()));
                            
                            HBox btnBox = new HBox(10, editBtn, deleteBtn);
                            
                            HBox contactBox = new HBox(infoBox, spacer, btnBox);
                            contactBox.setStyle("-fx-padding: 10px; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1px 0;");
                            
                            emergencyContactsContainer.getChildren().add(contactBox);
                        }
                    } else {
                        Label emptyLbl = new Label("No emergency contacts added.");
                        emptyLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px; -fx-font-style: italic;");
                        emergencyContactsContainer.getChildren().add(emptyLbl);
                    }
                }
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    nameLabel.setText("Unknown User");
                    emailLabel.setText("");
                    emergencyContactsContainer.getChildren().clear();
                    Label errLbl = new Label("No data available.");
                    errLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px; -fx-font-style: italic;");
                    emergencyContactsContainer.getChildren().add(errLbl);
                });
                return null;
            });
            
        ProfileService.getJourneyHistory()
            .thenAccept(history -> Platform.runLater(() -> {
                loadingOverlay.hide();
                historyContainer.getChildren().clear();
                if (history != null && history.length > 0) {
                    for (var h : history) {
                        String srcDest = "Journey #" + (10 + (int)(Math.random() * 50));
                        Label routeLbl = new Label(srcDest + " • " + h.getTravelMode());
                        routeLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #2B2B2B; -fx-font-weight: bold;");
                        Label scoreLbl = new Label("Safety " + h.getFinalConfidenceScore() + "% • " + (h.getCreatedAt() != null ? h.getCreatedAt().toString().substring(0, 10) : "Recent"));
                        scoreLbl.getStyleClass().add("subtitle");
                        scoreLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
                        
                        VBox hBox = new VBox(5, routeLbl, scoreLbl);
                        hBox.setStyle("-fx-padding: 10px 0; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1px 0;");
                        historyContainer.getChildren().add(hBox);
                    }
                } else {
                    Label emptyLbl = new Label("No journeys yet.");
                    emptyLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px; -fx-font-style: italic;");
                    historyContainer.getChildren().add(emptyLbl);
                }
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    loadingOverlay.hide();
                    historyContainer.getChildren().clear();
                    Label errLbl = new Label("Unable to load journey history.");
                    errLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px; -fx-font-style: italic;");
                    historyContainer.getChildren().add(errLbl);
                });
                return null;
            });
    }

    @FXML
    public void handleShowAddContactModal() {
        currentEditingContact = null;
        modalTitleLabel.setText("Add Emergency Contact");
        contactNameField.setText("");
        contactPhoneField.setText("");
        modalErrorLabel.setVisible(false);
        contactModalOverlay.setVisible(true);
    }

    private void handleShowEditContactModal(EmergencyContactDto contact) {
        currentEditingContact = contact;
        modalTitleLabel.setText("Edit Emergency Contact");
        contactNameField.setText(contact.getName());
        contactPhoneField.setText(contact.getPhoneNumber());
        modalErrorLabel.setVisible(false);
        contactModalOverlay.setVisible(true);
    }

    @FXML
    public void handleCloseModal() {
        contactModalOverlay.setVisible(false);
    }

    @FXML
    public void handleSaveContact() {
        String name = contactNameField.getText();
        String phone = contactPhoneField.getText();

        if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            modalErrorLabel.setText("Please fill all fields.");
            modalErrorLabel.setVisible(true);
            return;
        }

        modalSaveButton.setDisable(true);

        EmergencyContactDto dto = new EmergencyContactDto();
        dto.setName(name);
        dto.setPhoneNumber(phone);
        dto.setRelationship("Contact"); // Default

        if (currentEditingContact == null) {
            ProfileService.addEmergencyContact(dto)
                .thenAccept(res -> Platform.runLater(() -> {
                    modalSaveButton.setDisable(false);
                    contactModalOverlay.setVisible(false);
                    loadProfile();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        modalSaveButton.setDisable(false);
                        modalErrorLabel.setText("Failed to save contact.");
                        modalErrorLabel.setVisible(true);
                    });
                    return null;
                });
        } else {
            ProfileService.editEmergencyContact(currentEditingContact.getId(), dto)
                .thenAccept(res -> Platform.runLater(() -> {
                    modalSaveButton.setDisable(false);
                    contactModalOverlay.setVisible(false);
                    loadProfile();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        modalSaveButton.setDisable(false);
                        modalErrorLabel.setText("Failed to edit contact.");
                        modalErrorLabel.setVisible(true);
                    });
                    return null;
                });
        }
    }

    private void handleDeleteContact(Long id) {
        loadingOverlay.show();
        ProfileService.deleteEmergencyContact(id)
            .thenAccept(res -> Platform.runLater(this::loadProfile))
            .exceptionally(ex -> {
                Platform.runLater(() -> loadingOverlay.hide());
                return null;
            });
    }

    @FXML
    public void handleLogout() {
        SessionManager.logout();
        NavigationManager.navigate(Screen.LOGIN);
    }
    
    @FXML
    public void handleBack() {
        NavigationManager.navigate(Screen.HOME);
    }
}
