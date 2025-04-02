package com.capstone.controller;

import com.capstone.model.Faculty;
import com.capstone.service.FacultyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FacultyDashboardController {

    @FXML private Label facultyNameLabel;
    @FXML private Label departmentLabel;
    @FXML private Button viewTeamsButton;
    @FXML private Button logoutButton;

    private FacultyService facultyService; // Will be set manually in LoginController
    private String loggedInFacultyID;

    // Default constructor (required by FXMLLoader)
    public FacultyDashboardController() {}

    // Setter for manual service injection
    public void setFacultyService(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    public void setLoggedInFacultyID(String facultyID) {
        this.loggedInFacultyID = facultyID;
        loadFacultyDetails(); // Call this after facultyService is set
    }

    @FXML
    public void initialize() {
        System.out.println("FacultyDashboardController initialized!");
    }

    private void loadFacultyDetails() {
        if (facultyService == null) {
            System.out.println("Error: facultyService is null!");
            return;
        }

        Faculty faculty = facultyService.getFacultyByID(loggedInFacultyID);
        if (faculty != null) {
            facultyNameLabel.setText(faculty.getName());
            departmentLabel.setText(faculty.getDepartment());
        } else {
            facultyNameLabel.setText("Unknown");
            departmentLabel.setText("Unknown");
        }
    }

    @FXML
    private void handleViewTeams() {
        System.out.println("View Teams button clicked!");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
    }
}
