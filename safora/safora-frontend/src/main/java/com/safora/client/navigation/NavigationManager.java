package com.safora.client.navigation;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NavigationManager {
    private static Stage mainStage;
    private static final Stack<Screen> history = new Stack<>();
    private static final Map<String, Object> sessionData = new HashMap<>();

    public static void init(Stage stage) {
        mainStage = stage;
        mainStage.setTitle("Safora - Navigate Smarter. Arrive Safer.");
    }

    public static void navigate(Screen screen) {
        navigate(screen, null);
    }

    public static void navigate(Screen screen, Object data) {
        if (data != null) {
            sessionData.put(screen.name(), data);
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(screen.getFxmlPath()));
            Parent root = loader.load();
            
            // Fade Transition for premium feel
            root.setOpacity(0.0);
            
            if (mainStage.getScene() == null) {
                Scene scene = new Scene(root, 1024, 768);
                scene.getStylesheets().add(NavigationManager.class.getResource("/com/safora/client/css/theme.css").toExternalForm());
                scene.getStylesheets().add(NavigationManager.class.getResource("/com/safora/client/css/layout.css").toExternalForm());
                mainStage.setScene(scene);
                mainStage.show();
            } else {
                mainStage.getScene().setRoot(root);
            }
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            history.push(screen);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load screen: " + screen, e);
        }
    }

    public static void goBack() {
        if (history.size() > 1) {
            history.pop(); // Remove current
            Screen previous = history.peek();
            navigate(previous);
        }
    }

    public static Object getContextData(Screen screen) {
        return sessionData.get(screen.name());
    }
}
