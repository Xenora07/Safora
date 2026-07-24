module com.safora.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;

    opens com.safora.client to javafx.fxml;
    opens com.safora.client.controllers to javafx.fxml;
    opens com.safora.client.components to javafx.fxml;
    opens com.safora.client.dto to com.fasterxml.jackson.databind;
    
    exports com.safora.client;
    exports com.safora.client.controllers;
    exports com.safora.client.components;
    exports com.safora.client.dto;
}
