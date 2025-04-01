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
import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(ApplicationContext context) {
        this.authService = context.getBean(AuthService.class);
    }

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
        String userID = usernameField.getText();
        String teamID = teamIDField.getText();
        String password = passwordField.getText();

        if (userID.isBlank() || password.isBlank()) {
            errorLabel.setText("User ID and Password are required.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String authResult = authService.authenticateUser(userID, teamID, password);

        switch (authResult) {
            case "STUDENT":
                switchToStudentDashboard(event);
                break;
            case "FACULTY":
                switchToFacultyDashboard(event);
                break;
            case "MISSING_TEAM":
                errorLabel.setText("Team ID is required for students.");
                errorLabel.setStyle("-fx-text-fill: red;");
                break;
            default:
                errorLabel.setText("Invalid credentials. Please try again.");
                errorLabel.setStyle("-fx-text-fill: red;");
                break;
        }
    }

    @FXML
    private void switchToStudentDashboard(ActionEvent event) {
        switchDashboard(event, "/views/student_dashboard.fxml", "Student Dashboard");
    }

    @FXML
    private void switchToFacultyDashboard(ActionEvent event) {
        switchDashboard(event, "/views/faculty_dashboard.fxml", "Faculty Dashboard");
    }

    private void switchDashboard(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}

