package com.capstone.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

import com.capstone.service.AdminService;

@Controller
public class AdminDashboardController {

    @FXML private VBox sidebar;
    @FXML private Button menuButton;

    @FXML private Label adminIDLabel;
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;
    
    @FXML private Label homeLink;
    @FXML private Label usersLink;
    @FXML private Label reviewScheduleLink;
    @FXML private Label resultsLink;
    @FXML private Label logoutLink;

    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;
    private String loggedInAdminID;

    public AdminDashboardController() {}

    private AdminService adminService;

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

public void setLoggedInAdminID(String adminID) {
    this.loggedInAdminID = adminID;
    loadAdminDetails();
}


    @FXML
    public void initialize() {
        System.out.println("AdminDashboardController initialized!");
    }

    private void loadAdminDetails() {
        if (adminService == null) {
            System.out.println("Error: adminService is null!");
            return;
        }
    
        if (loggedInAdminID == null) {
            System.out.println("Error: loggedInAdminID is null!");
            return;
        }
    
        adminService.getAdminById(loggedInAdminID).ifPresentOrElse(admin -> {
            adminIDLabel.setText(admin.getUserID());
            adminNameLabel.setText(admin.getName());
            adminEmailLabel.setText(admin.getEmail());
        }, () -> {
            adminIDLabel.setText("Unknown");
            adminNameLabel.setText("Unknown");
            adminEmailLabel.setText("Unknown");
        });
    }
    

    @FXML
    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);

        if (isSidebarOpen) {
            transition.setToX(-200); // Hide
        } else {
            transition.setToX(0); // Show
        }

        transition.play();
        isSidebarOpen = !isSidebarOpen;
    }

    @FXML
    private void handleNavigation(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        String section = clickedLabel.getText();

        switch (section) {
            case "Home":
                System.out.println("Navigating to Admin Dashboard... (TODO)");
                break;
            case "Manage Users":
                handleManageUsers();
                break;
            case "Review Schedule":
                System.out.println("Navigating to Review Schedule... (TODO)");
                break;
            case "Results":
                System.out.println("Navigating to Results... (TODO)");
                break;
            case "Logout":
                handleLogout();
                break;
            default:
                System.out.println("Unknown section clicked: " + section);
        }
    }

    @FXML
    private void handleManageUsers() {
        // TODO: Implement user management UI
        System.out.println("Opening Manage Users section... (TODO)");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out... (TODO)");
        // TODO: Implement actual logout logic and redirect to login page
    }
}
