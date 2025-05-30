package com.capstone.controller;

import com.capstone.service.AuthService;
import com.capstone.service.FacultyService; // Import FacultyService
import com.capstone.service.TeamService;
import com.capstone.service.AdminService;
import com.capstone.service.PhaseSubmissionService;

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

    private AuthService authService;
    private ApplicationContext context;

    // Spring context to fetch beans

    public LoginController() {
        this.authService = null;
        this.context = null;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
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
            case "ADMIN":
                switchToAdminDashboard(event);
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
    
            // Retrieve services from ApplicationContext
            TeamService teamService = context.getBean(TeamService.class);
            PhaseSubmissionService submissionService = context.getBean(PhaseSubmissionService.class);
    
            // Inject services
            controller.setTeamService(teamService);
            controller.setSubmissionService(submissionService);
    
            // Inject PhaseSubmissionController
            PhaseSubmissionController phaseSubmissionController = new PhaseSubmissionController(submissionService, teamService);
            controller.setPhaseSubmissionController(phaseSubmissionController);
    
            // Pass student ID
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
            FacultyService facultyService = context.getBean(FacultyService.class);
            PhaseSubmissionService submissionService = context.getBean(PhaseSubmissionService.class);
            controller.setFacultyService(facultyService);
            controller.setPhaseSubmissionService(submissionService);

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

    @FXML
    private void switchToAdminDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller if needed for setting Admin ID or services
            AdminDashboardController controller = loader.getController();
            AdminService adminService = context.getBean(AdminService.class);
            TeamService teamService = context.getBean(TeamService.class);
            controller.setAdminService(adminService);
            controller.setTeamService(teamService);
            controller.setLoggedInAdminID(usernameField.getText()); // Assuming method exists

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Error loading admin dashboard.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
