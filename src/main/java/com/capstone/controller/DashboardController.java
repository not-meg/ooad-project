package com.capstone.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;


public class DashboardController {

    @FXML private Label teamIDLabel;
    @FXML private Label problemStatementLabel;
    @FXML private Label facultyLabel;
    @FXML private VBox sidebar;

    @FXML private Label dashboardLink;
    @FXML private Label profileLink;
    @FXML private Label settingsLink;
    
    private boolean isSidebarOpen = false;

    // Placeholder team details (Replace with actual backend data later)
    private String teamID = "T12345";
    private String problemStatement = "Automated Grading System";
    private String facultyName = "Dr. A. Sharma";

    @FXML
    public void initialize() {
        teamIDLabel.setText(teamID);
        problemStatementLabel.setText(problemStatement);
        facultyLabel.setText(facultyName);
    }

    @FXML
    private void handleNavigation(javafx.scene.input.MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        String section = clickedLabel.getText();

        switch (section) {
            case "Home":
                System.out.println("Navigating to Dashboard... (TODO: Implement navigation)");
                break;
            case "Profile":
                System.out.println("Navigating to Profile... (TODO: Implement navigation)");
                break;
            case "Notification":
                System.out.println("Navigating to Notification... (TODO: Implement navigation)");
                break;
            case "Submission":
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/submission.fxml"));
        Parent submissionRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(submissionRoot));
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
    break;

            case "Team":
                System.out.println("Navigating to Team... (TODO: Implement navigation)");
                break;
            case "Mentor Feedback":
                System.out.println("Navigating to Mentor Feedback... (TODO: Implement navigation)");
                break;
            case "Results":
                System.out.println("Navigating to Results... (TODO: Implement navigation)");
                break;
            case "Review Schedule":
                System.out.println("Navigating to Review Schedule... (TODO: Implement navigation)");
                break;
            case "Logout":
                 System.out.println("Logging out");
                 break;
            default:
                System.out.println("Unknown section clicked");
                break;
        }
    }

    @FXML
    private void handleViewTeam(ActionEvent event) {
        System.out.println("View Team button clicked!");
        // Implement navigation logic here (e.g., open a new window)
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out");
        // Implement navigation logic here (e.g., open a new window)
    }


    @FXML
    private void toggleSidebar(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);

        if (isSidebarOpen) {
            transition.setToX(-200); // Move sidebar out
        } else {
            transition.setToX(0); // Move sidebar in
        }

        transition.play();
        isSidebarOpen = !isSidebarOpen;
    }
}
