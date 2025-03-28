package com.capstone.controller;

import com.capstone.service.AuthService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField teamIDField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String studentID = usernameField.getText();
        String teamID = teamIDField.getText();
        String teamPassword = passwordField.getText();

        if (authService.authenticateUser(studentID, teamID, teamPassword)) {
            switchToDashboard(event);
        } else {
            errorLabel.setText("Invalid credentials. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void switchToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
