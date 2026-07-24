package com.safora.client.navigation;

public enum Screen {
    LOGIN("/com/safora/client/views/login.fxml"),
    REGISTER("/com/safora/client/views/register.fxml"),
    HOME("/com/safora/client/views/home.fxml"),
    ROUTE_RECOMMENDATION("/com/safora/client/views/route_recommendation.fxml"),
    LIVE_NAVIGATION("/com/safora/client/views/live_navigation.fxml"),
    JOURNEY_SUMMARY("/com/safora/client/views/journey_summary.fxml"),
    COMMUNITY_REPORTS("/com/safora/client/views/community_reports.fxml"),
    PROFILE("/com/safora/client/views/profile.fxml");

    private final String fxmlPath;

    Screen(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}