package com.capstone.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label teamIDLabel;
    @FXML private Label problemStatementLabel;
    @FXML private Label facultyLabel;
    @FXML private Button viewTeamButton;
    @FXML private Button logoutButton;

    // Placeholder team details (Replace with actual backend data later)
    private String teamID = "T12345";
    private String problemStatement = "Automated Grading System";
    private String facultyName = "Dr. A. Sharma";

    @FXML
    public void initialize() {
        // Set placeholders for team details
        teamIDLabel.setText(teamID);
        problemStatementLabel.setText(problemStatement);
        facultyLabel.setText(facultyName);
    }

    @FXML
    private void handleViewTeam() {
        System.out.println("View Team button clicked! (TODO: Implement navigation)");
        // TODO: Navigate to the detailed team view page
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out... (TODO: Implement logout)");
        // TODO: Implement logout logic & redirect to login screen
    }

    @FXML
private void handleSubmitPhaseX(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Submit Phase X Document");
    alert.setHeaderText(null);
    alert.setContentText("Feature to submit Phase X document is under development!");
    alert.showAndWait();
}

}
