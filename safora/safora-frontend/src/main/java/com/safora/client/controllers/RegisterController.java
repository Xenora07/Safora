package com.safora.client.controllers;

import com.safora.client.components.LoadingOverlay;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStrengthLabel;
    @FXML private Label errorLabel;
    @FXML private StackPane rootPane;

    private LoadingOverlay loadingOverlay;

    @FXML
    public void initialize() {
        loadingOverlay = new LoadingOverlay();
        rootPane.getChildren().add(loadingOverlay);

        if (passwordField != null && passwordStrengthLabel != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> updatePasswordStrength(newVal));
        }
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrengthLabel.setText("");
            return;
        }
        if (password.length() < 6) {
            passwordStrengthLabel.setText("Strength: Weak");
            passwordStrengthLabel.setStyle("-fx-text-fill: #F56565; -fx-font-size: 12px;");
        } else if (password.length() < 10) {
            passwordStrengthLabel.setText("Strength: Medium");
            passwordStrengthLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px;");
        } else {
            passwordStrengthLabel.setText("Strength: Strong");
            passwordStrengthLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px;");
        }
    }

    @FXML
    public void handleRegister() {
        String name = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : password;

        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            phone == null || phone.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            errorLabel.setText("All fields are required.");
            errorLabel.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        loadingOverlay.show();

        AuthService.register(name, email, password, phone)
            .thenAccept(response -> Platform.runLater(() -> {
                loadingOverlay.hide();
                // "Successful registration returns to Login"
                NavigationManager.navigate(Screen.LOGIN);
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    loadingOverlay.hide();
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("Exception: ")) {
                        msg = msg.substring(msg.indexOf("Exception: ") + 11);
                    } else if (msg != null && msg.contains("RuntimeException: ")) {
                        msg = msg.substring(msg.indexOf("RuntimeException: ") + 18);
                    } else if (msg == null || msg.isEmpty()) {
                        msg = "Registration failed.";
                    }
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                });
                return null;
            });
    }

    @FXML
    public void navigateToLogin() {
        NavigationManager.navigate(Screen.LOGIN);
    }
}
