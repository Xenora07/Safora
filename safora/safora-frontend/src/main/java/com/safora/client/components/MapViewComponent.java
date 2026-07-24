package com.safora.client.components;

import javafx.concurrent.Worker;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.application.Platform;

import java.net.URL;

public class MapViewComponent extends StackPane {

    private final WebView webView;
    private final WebEngine webEngine;
    private final LeafletBridge leafletBridge;

    public MapViewComponent() {
        this.webView = new WebView();
        this.webEngine = webView.getEngine();
        this.leafletBridge = new LeafletBridge();

        // Ensure the WebView always matches the exact size of the MapViewComponent
        webView.prefWidthProperty().bind(this.widthProperty());
        webView.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().add(webView);

        setupWebEngine();

        // Listen for layout changes and explicitly tell Leaflet to invalidate size
        this.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                // Run after a slight delay on the UI thread to ensure DOM is ready
                Platform.runLater(() -> {
                    try {
                        webEngine.executeScript("if (typeof map !== 'undefined' && map) { map.invalidateSize(); }");
                    } catch (Exception e) {
                        // ignore if map is not fully initialized
                    }
                });
            }
        });
    }

    private void setupWebEngine() {
        // Load the local HTML file containing Leaflet map
        URL url = getClass().getResource("/com/safora/client/web/map.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());
        }

        // Expose Java bridge to JavaScript when page loads
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
                    window.setMember("javaBridge", leafletBridge);
                } catch (Exception e) {}
                // Call JS initialization if needed
                webEngine.executeScript("if (typeof initMap === 'function') { initMap(); }");
                // Immediately invalidate size to fix initialization grey areas
                webEngine.executeScript("setTimeout(function() { if (typeof map !== 'undefined' && map) { map.invalidateSize(); } }, 100);");
            }
        });
    }

    /**
     * Centers the map on the given latitude and longitude.
     */
    public void setCenter(double lat, double lng) {
        if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            webEngine.executeScript(String.format("setCenter(%f, %f);", lat, lng));
        }
    }

    /**
     * Adds or updates the current location marker.
     */
    public void setCurrentLocation(double lat, double lng) {
        if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            webEngine.executeScript(String.format("setCurrentLocation(%f, %f);", lat, lng));
        }
    }

    /**
     * Adds a generic marker.
     */
    public void addMarker(double lat, double lng, String type) {
        if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            webEngine.executeScript(String.format("addMarker(%f, %f, '%s');", lat, lng, type));
        }
    }

    public void addCheckpointMarker(double lat, double lng, String type) {
        if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            webEngine.executeScript(String.format("addCheckpointMarker(%f, %f, '%s');", lat, lng, type));
        }
    }

    public WebEngine getEngine() {
        return webEngine;
    }
}
