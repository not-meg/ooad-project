package com.capstone.controller;

import com.capstone.model.Team;
import com.capstone.service.FacultyService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FacultyDashboardController {

    @FXML private VBox sidebar;
    @FXML private Button menuButton;

    @FXML private Label facultyNameLabel;
    @FXML private Label departmentLabel;

    @FXML private Label homeLink;
    @FXML private Label teamsLink;
    @FXML private Label reviewLink;
    @FXML private Label feedbackLink;
    @FXML private Label resultsLink;
    @FXML private Label logoutLink;

    @FXML private Button viewTeamsButton;
    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;

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
                System.out.println("Navigating to Faculty Dashboard... (TODO)");
                break;
            case "Teams":
                handleViewTeams();
                break;
            case "Review Schedule":
                System.out.println("Navigating to Review Schedule... (TODO)");
                break;
            case "Feedback":
                System.out.println("Navigating to Feedback... (TODO)");
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
    private void handleViewTeams() {
        if (facultyService == null) {
            System.out.println("Error: facultyService is null!");
            return;
        }

        List<Team> teams = facultyService.getTeamsByFacultyID(loggedInFacultyID);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Assigned Teams");

        ListView<String> teamListView = new ListView<>();

        if (teams.isEmpty()) {
            teamListView.getItems().add("No teams assigned.");
        } else {
            for (Team team : teams) {
                String teamInfo = "Team ID: " + team.getTeamID() +
                                  "\nProblem: " + team.getProblemStatement() +
                                  "\nStudents: " + String.join(", ", team.getStudentIDs()) +
                                  "\n--------------------------------";
                teamListView.getItems().add(teamInfo);
            }
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(teamListView);

        Scene scene = new Scene(layout, 500, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        // TODO: Implement logout navigation
    }
}
