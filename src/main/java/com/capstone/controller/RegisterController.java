package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;

public class RegisterController {

    @FXML private TextField fullName1, email1, srn1, fullName2, email2, srn2;
    @FXML private TextField fullName3, email3, srn3, fullName4, email4, srn4;
    @FXML private Label gender1, gender2, gender3, gender4;
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
        if (!validateFields()) {
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your team has been registered.");
    }

    private boolean validateFields() {
        if (fullName1.getText().isEmpty() || email1.getText().isEmpty() || srn1.getText().isEmpty() || gender1.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all Member 1 details including gender.");
            return false;
        }

        if (guideName.getText().isEmpty() || projectTitle.getText().isEmpty() || projectDomain.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please complete all project details.");
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
}
