package com.capstone.controller;

import com.capstone.model.Team;
import com.capstone.service.TeamService;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.Optional;


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
    private String teamID;
    private String problemStatement;
    private String facultyName;

    private TeamService teamService;
    public DashboardController() {}

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    private String loggedInStudentID;

    public void setLoggedInStudentID(String studentID) {
        this.loggedInStudentID = studentID;
        loadTeamDetails(); // Load data once ID is set
    }


    @FXML
    public void initialize() {
        teamIDLabel.setText(teamID);
        problemStatementLabel.setText(problemStatement);
        facultyLabel.setText(facultyName);
    }

    private void loadTeamDetails() {
        if (teamService == null) {
            System.out.println("❌ TeamService is null");
            return;
        }
    
        if (loggedInStudentID == null) {
            System.out.println("❌ loggedInStudentID is null");
            return;
        }

        System.out.println("Trying to fetch team for student: " + loggedInStudentID);
    
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            teamIDLabel.setText(team.getTeamID());
            problemStatementLabel.setText(team.getProblemStatement());
            facultyLabel.setText(team.getFacultyID()); // or fetch actual name if needed
        } else {
            teamIDLabel.setText("N/A");
            problemStatementLabel.setText("No problem assigned.");
            facultyLabel.setText("No faculty assigned.");
            System.out.println("❌ No team found for student: " + loggedInStudentID);
        }
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
        if (teamService == null) {
            System.out.println("❌ TeamService is not initialized!");
            return;
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            // ✅ Build the team details string
            StringBuilder details = new StringBuilder();
            details.append("Team ID: ").append(team.getTeamID()).append("\n");
            details.append("Problem Statement: ").append(team.getProblemStatement()).append("\n");
            details.append("Faculty ID: ").append(team.getFacultyID()).append("\n");
            details.append("Student IDs:\n");

            for (String member : team.getStudentIDs()) {
                details.append("- ").append(member).append("\n");
            }

            // ✅ Show it in an Alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Team Details");
            alert.setHeaderText("Here's your awesome team 👇");
            alert.setContentText(details.toString());
            alert.showAndWait();

            System.out.println("✅ Team details loaded and shown in popup.");
        } else {
            System.out.println("❌ Team not found!");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Team Not Found");
            alert.setHeaderText(null);
            alert.setContentText("You are not part of any team yet.");
            alert.showAndWait();
        }
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
