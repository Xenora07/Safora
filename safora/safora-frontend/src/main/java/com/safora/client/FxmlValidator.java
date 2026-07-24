package com.safora.client;

import com.safora.client.navigation.Screen;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxmlValidator {
    public static void main(String[] args) {
        Platform.startup(() -> {
            boolean allPassed = true;
            for (Screen screen : Screen.values()) {
                try {
                    System.out.println("Loading: " + screen.getFxmlPath());
                    FXMLLoader loader = new FXMLLoader(FxmlValidator.class.getResource(screen.getFxmlPath()));
                    Parent root = loader.load();
                    System.out.println("SUCCESS: " + screen.name());
                } catch (Exception e) {
                    System.err.println("FAILED: " + screen.name());
                    e.printStackTrace();
                    allPassed = false;
                }
            }
            if (!allPassed) {
                System.exit(1);
            } else {
                System.out.println("ALL FXML FILES LOADED SUCCESSFULLY.");
                System.exit(0);
            }
        });
    }
}
