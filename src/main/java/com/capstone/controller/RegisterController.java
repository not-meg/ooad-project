package com.capstone.controller;

import com.capstone.model.Student;
import com.capstone.service.TeamService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.util.List;

import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;

@Component // Marks this class as a Spring-managed component
public class RegisterController {

    private final TeamService teamService; // Dependency Injection

    // Constructor-based Dependency Injection
    // @Autowired
    public RegisterController(TeamService teamService) {
        this.teamService = teamService;
    }

    @FXML
    private TextField srn1, srn2, srn3, srn4;
    // @FXML private TextField fullName1, email1, fullName2, email2, fullName3,
    // email3, fullName4, email4;
    // @FXML private Label gender1, gender2, gender3, gender4;
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

    // Add event listeners to retrieve student details when SRN is entered
    /*
     * srn1.textProperty().addListener(
     * (observable, oldValue, newValue) -> fetchStudentDetails(newValue, fullName1,
     * email1, gender1));
     * srn2.textProperty().addListener(
     * (observable, oldValue, newValue) -> fetchStudentDetails(newValue, fullName2,
     * email2, gender2));
     * srn3.textProperty().addListener(
     * (observable, oldValue, newValue) -> fetchStudentDetails(newValue, fullName3,
     * email3, gender3));
     * srn4.textProperty().addListener(
     * (observable, oldValue, newValue) -> fetchStudentDetails(newValue, fullName4,
     * email4, gender4));
     * }
     */

    /*
     * private void fetchStudentDetails(String srn, TextField fullNameField,
     * TextField emailField, Label genderLabel) {
     * if (srn.isEmpty()) {
     * fullNameField.setText("");
     * emailField.setText("");
     * genderLabel.setText("");
     * return;
     * }
     * 
     * Student student = teamService.getStudentBySRN(srn);
     * if (student != null) {
     * fullNameField.setText(student.getName());
     * emailField.setText(student.getEmail());
     * genderLabel.setText(student.getGender().toString());
     * } else {
     * fullNameField.setText("Not Found");
     * emailField.setText("Not Found");
     * genderLabel.setText("Not Found");
     * }
     * }
     */

    @FXML
    private void handleRegisterButton() {
        if (!validateFields()) {
            return;
        }

        String teamID = "TEAM_" + srn1.getText(); // Generate a simple team ID
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
