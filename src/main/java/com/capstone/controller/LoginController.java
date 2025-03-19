package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        // For now, allow login without credentials
        switchToDashboard(event);
    }

    @FXML
    private void switchToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            // Ensure event source is valid
            Node source = (Node) event.getSource();
            if (source == null) {
                errorLabel.setText("Error: Event source is null.");
                return;
            }

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            System.out.println("FXML Load Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
