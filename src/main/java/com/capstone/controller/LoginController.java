package com.capstone.controller;

import com.capstone.service.AuthService;
import com.capstone.service.FacultyService; // Import FacultyService
import com.capstone.service.TeamService;

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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Controller
public class LoginController {

    private final AuthService authService;
    private final ApplicationContext applicationContext; // Spring context to fetch beans

    @Autowired
    public LoginController(AuthService authService, ApplicationContext applicationContext) {
        this.authService = authService;
        this.applicationContext = applicationContext;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/student_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller
            DashboardController controller = loader.getController();

            // Retrieve TeamService from ApplicationContext
            TeamService teamService = applicationContext.getBean(TeamService.class);
            controller.setTeamService(teamService);

            // Pass logged-in student ID
            controller.setLoggedInStudentID(usernameField.getText());

            // Switch scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Student Dashboard");
            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Error loading student dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    @FXML
    private void switchToFacultyDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/faculty_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller
            FacultyDashboardController controller = loader.getController();

            // Retrieve FacultyService from ApplicationContext
            FacultyService facultyService = applicationContext.getBean(FacultyService.class);
            controller.setFacultyService(facultyService);

            // Pass logged-in faculty ID
            controller.setLoggedInFacultyID(usernameField.getText());

            // Switch scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Faculty Dashboard");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading faculty dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
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
