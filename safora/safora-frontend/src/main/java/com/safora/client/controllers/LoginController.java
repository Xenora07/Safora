package com.safora.client.controllers;

import com.safora.client.components.LoadingOverlay;
import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import com.safora.client.services.AuthService;
import com.safora.client.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private StackPane rootPane;

    private LoadingOverlay loadingOverlay;

    @FXML
    public void initialize() {
        loadingOverlay = new LoadingOverlay();
        rootPane.getChildren().add(loadingOverlay);
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        loadingOverlay.show();

        AuthService.login(email, password)
            .orTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
            .thenAccept(response -> Platform.runLater(() -> {
                loadingOverlay.hide();
                SessionManager.setSession(response.getUserId(), response.getToken());
                NavigationManager.navigate(Screen.HOME);
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    loadingOverlay.hide();
                    if (ex instanceof java.util.concurrent.TimeoutException || ex.getCause() instanceof java.util.concurrent.TimeoutException) {
                        errorLabel.setText("Unable to reach the server. Please try again.");
                    } else {
                        String msg = ex.getMessage();
                        if (msg != null && msg.contains("Exception: ")) {
                            msg = msg.substring(msg.indexOf("Exception: ") + 11);
                        } else if (msg != null && msg.contains("RuntimeException: ")) {
                            msg = msg.substring(msg.indexOf("RuntimeException: ") + 18);
                        } else if (msg == null || msg.isEmpty()) {
                            msg = "Login failed. Check credentials.";
                        }
                        errorLabel.setText(msg);
                    }
                    errorLabel.setVisible(true);
                });
                return null;
            });
    }

    @FXML
    public void navigateToRegister() {
        NavigationManager.navigate(Screen.REGISTER);
    }
}
