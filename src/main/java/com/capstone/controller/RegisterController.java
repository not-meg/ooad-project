package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import java.io.IOException;

public class RegisterController {
    
    @FXML private TextField fullName1, email1, srn1;
    @FXML private TextField fullName2, email2, srn2;
    @FXML private TextField fullName3, email3, srn3;
    @FXML private TextField fullName4, email4, srn4;
    @FXML private TextField guideName, projectTitle;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private ComboBox<String> projectDomain;

    @FXML
    public void initialize() {
        projectDomain.setItems(FXCollections.observableArrayList(
            "Cybersecurity", "Machine Learning", "Cloud Computing", "IoT", "Digital Twin"
        ));
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        // Validate input fields
        if (!validateFields()) {
            return;
        }

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your team has been registered.");

        // Switch to login page
        switchToLoginPage(event);
    }

    private boolean validateFields() {
        // At least Member 1 details should be filled
        if (fullName1.getText().isEmpty() || email1.getText().isEmpty() || srn1.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in at least Member 1 details.");
            return false;
        }
        // Ensure project details are filled
        if (guideName.getText().isEmpty() || projectTitle.getText().isEmpty() || projectDomain.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please complete all project details.");
            return false;
        }
        // Password validation
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

    private void switchToLoginPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the Login page.");
        }
    }
}
