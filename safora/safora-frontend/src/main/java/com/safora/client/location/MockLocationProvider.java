package com.safora.client.location;

public class MockLocationProvider implements LocationProvider {
    @Override
    public Location getCurrentLocation() {
        return new Location(19.0441, 72.9103); // Mocking Mumbai (Source)
    }

    @Override
    public Location resolveDestination(String query) {
        return new Location(18.9440, 72.8228); 
    }

    @Override
    public javafx.beans.property.ReadOnlyObjectProperty<Location> locationProperty() {
        return new javafx.beans.property.SimpleObjectProperty<>(new Location(19.0441, 72.9103));
    }
}
