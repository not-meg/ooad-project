package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Component;

@Component
public class DashboardController {

    @FXML
    public void handleStartButton() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Capstone System");
        alert.setHeaderText(null);
        alert.setContentText("Starting your project...");
        alert.showAndWait();
    }
}
