package com.capstone.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HomepageController {

    @FXML
    private void handleLoginButton(ActionEvent event) {
        try {
            // Load the login page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
