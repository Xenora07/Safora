package com.safora.client;

import com.safora.client.navigation.NavigationManager;
import com.safora.client.navigation.Screen;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        NavigationManager.init(primaryStage);
        // We will start at LOGIN
        NavigationManager.navigate(Screen.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
