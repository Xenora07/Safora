package com.safora.client.location;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface LocationProvider {
    Location getCurrentLocation();
    Location resolveDestination(String query);
    ReadOnlyObjectProperty<Location> locationProperty();
}
