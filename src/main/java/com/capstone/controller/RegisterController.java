package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;

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
        if (fullName1.getText().isEmpty() || email1.getText().isEmpty() || srn1.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in at least Member 1 details.");
            return;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your team has been registered.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
