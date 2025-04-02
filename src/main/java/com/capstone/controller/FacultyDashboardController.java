package com.capstone.controller;

import com.capstone.model.Team;
import com.capstone.service.FacultyService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FacultyDashboardController {

    @FXML private Label facultyNameLabel;
    @FXML private Label departmentLabel;
    @FXML private Button viewTeamsButton;
    @FXML private Button logoutButton;
    @FXML private ListView<String> teamsListView; // ListView to show teams

    private FacultyService facultyService;
    private String loggedInFacultyID;

    public FacultyDashboardController() {}

    public void setFacultyService(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    public void setLoggedInFacultyID(String facultyID) {
        this.loggedInFacultyID = facultyID;
        loadFacultyDetails();
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

        var faculty = facultyService.getFacultyByID(loggedInFacultyID);
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
        if (facultyService == null) {
            System.out.println("Error: facultyService is null!");
            return;
        }

        List<Team> teams = facultyService.getTeamsByFacultyID(loggedInFacultyID);

        // Create a new Stage (pop-up window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Assigned Teams");

        // Create ListView to display teams
        ListView<String> teamListView = new ListView<>();

        if (teams.isEmpty()) {
            teamListView.getItems().add("No teams assigned.");
        } else {
            for (Team team : teams) {
                StringBuilder teamInfo = new StringBuilder();
                teamInfo.append("Team ID: ").append(team.getTeamID())
                        .append("\nProblem: ").append(team.getProblemStatement())
                        .append("\nStudents: ").append(String.join(", ", team.getStudentIDs()))
                        .append("\n--------------------------------");
                
                teamListView.getItems().add(teamInfo.toString());
            }
        }

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(teamListView);

        // Scene and Stage setup
        Scene scene = new Scene(layout, 500, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait(); // Show the pop-up and wait for it to close
    }
   

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
    }
}
