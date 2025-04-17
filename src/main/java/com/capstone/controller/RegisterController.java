package com.capstone.controller;

import com.capstone.service.AuthService;
import com.capstone.service.TeamService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.List;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;

@Component // Marks this class as a Spring-managed component
public class RegisterController {
    private final ApplicationContext context;

    private final TeamService teamService;
    @FXML
    private Button BackTologinButton;
    // Dependency Injection

    // Constructor-based Dependency Injection
    // @Autowired
    public RegisterController(ApplicationContext context, TeamService teamService) {
        this.context = context;
        this.teamService = teamService;
    }

    @FXML
    private TextField srn1, srn2, srn3, srn4;

    @FXML
    private TextField guideName, projectTitle;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private ComboBox<String> projectDomain;

    @FXML
    public void initialize() {
        projectDomain.setItems(FXCollections.observableArrayList(
                "Cybersecurity", "Machine Learning", "Cloud Computing", "IoT", "Digital Twin"));
    }

    @FXML
    private void handleRegisterButton() {
        if (!validateFields()) {
            return;
        }

        String teamID = teamService.generateNextTeamID(); // Generate a simple team ID
        String facultyID = guideName.getText(); // Assuming faculty name is the faculty ID
        String problemStatement = projectTitle.getText();
        String password = passwordField.getText();

        List<String> studentSRNs = List.of(srn1.getText(), srn2.getText(), srn3.getText(), srn4.getText());

        // Call service method to register team
        boolean success = teamService.registerTeam(teamID, problemStatement, facultyID, studentSRNs, password);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your team has been registered.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please check the details and try again.");
        }
    }

    private boolean validateFields() {
        if (srn1.getText().isEmpty() || srn2.getText().isEmpty() || srn3.getText().isEmpty()
                || srn4.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All student SRNs must be entered.");
            return false;
        }

        if (guideName.getText().isEmpty() || projectTitle.getText().isEmpty() || projectDomain.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please completegit  all project details.");
            return false;
        }

        if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Password fields cannot be empty.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoBackToLogin() {
        try {
            // Load the FXML for login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Get the controller and inject dependencies
            LoginController controller = loader.getController();

            // Manually inject Spring dependencies
            controller.setAuthService(context.getBean(AuthService.class));
            controller.setApplicationContext(context); // ✅ inject Spring context

            // Set the new scene with the login screen
            Scene scene = new Scene(root);
            Stage stage = (Stage) BackTologinButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions accordingly
        }
    }

}
