package com.safora.client.components;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class LoadingOverlay extends StackPane {
    
    private Label textLabel;

    public LoadingOverlay() {
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        this.setAlignment(Pos.CENTER);
        
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);
        
        textLabel = new Label();
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 20 0 0 0;");
        textLabel.setVisible(false);
        textLabel.setManaged(false);
        
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(15, spinner, textLabel);
        box.setAlignment(Pos.CENTER);
        
        this.getChildren().add(box);
        this.setVisible(false);
    }
    
    public void setText(String text) {
        javafx.application.Platform.runLater(() -> {
            if (text != null && !text.isEmpty()) {
                textLabel.setText(text);
                textLabel.setVisible(true);
                textLabel.setManaged(true);
            } else {
                textLabel.setVisible(false);
                textLabel.setManaged(false);
            }
        });
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
        setText(null);
    }
}
