package com.capstone.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FacultyDashboardController {

    @FXML private Label facultyNameLabel;
    @FXML private Label departmentLabel;
    @FXML private Button viewTeamsButton;
    @FXML private Button logoutButton;

    // Placeholder faculty info (Replace with actual backend data later)
    private String facultyName = "Dr. A. Sharma";
    private String department = "Computer Science";

    @FXML
    public void initialize() {
        // Set placeholders for faculty details
        facultyNameLabel.setText(facultyName);
        departmentLabel.setText(department);
    }

    @FXML
    private void handleViewTeams() {
        System.out.println("View Teams button clicked! (TODO: Implement team view)");
        // TODO: Navigate to the detailed teams view page
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out... (TODO: Implement logout)");
        // TODO: Implement logout logic & redirect to login screen
    }
}
