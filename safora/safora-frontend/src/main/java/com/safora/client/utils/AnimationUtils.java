package com.safora.client.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

    public static void fadeNodeIn(Node node, double durationMs) {
        node.setOpacity(0);
        node.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void fadeNodeOut(Node node, double durationMs) {
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> node.setVisible(false));
        ft.play();
    }

    public static void slideUpAndFadeIn(Node node, double durationMs, double yOffset) {
        node.setOpacity(0);
        node.setTranslateY(yOffset);
        node.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }
    
    public static void slideDownAndFadeOut(Node node, double durationMs, double yOffset) {
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setToValue(0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setToY(yOffset);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setOnFinished(e -> node.setVisible(false));
        pt.play();
    }

    public static void applyHoverElevate(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        });
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
