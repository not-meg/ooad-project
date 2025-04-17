package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.DriveUploader;
import com.capstone.service.FacultyService;
import com.capstone.service.PhaseSubmissionService;
import com.capstone.CapstoneApplication;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.io.File;

@Controller
public class FacultyDashboardController {

    @FXML private VBox sidebar;
    @FXML private Button menuButton;

    @FXML private Label facultyNameLabel;
    @FXML private Label departmentLabel;
    @FXML private Label designationLabel;

    @FXML private Label homeLink;
    @FXML private Label teamsLink;
    @FXML private Label reviewLink;
    @FXML private Label feedbackLink;
    @FXML private Label resultsLink;
    @FXML private Label logoutLink;

    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;

    private FacultyService facultyService;
    private PhaseSubmissionService submissionService;

    private String loggedInFacultyID;

    public FacultyDashboardController() {}

    public void setFacultyService(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    public void setPhaseSubmissionService(PhaseSubmissionService submissionService) {
        this.submissionService = submissionService;
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
            designationLabel.setText(faculty.getDesignation());
        } else {
            facultyNameLabel.setText("Unknown");
            departmentLabel.setText("Unknown");
            designationLabel.setText("Unknown");
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

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);

        if (teams.isEmpty()) {
            layout.getChildren().add(new Label("No teams assigned."));
        } else {
            for (Team team : teams) {
                VBox teamBox = new VBox(5);
                teamBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f8f8f8;");

                Label teamInfo = new Label("Team ID: " + team.getTeamID() +
                                        "\nProblem: " + team.getProblemStatement() +
                                        "\nStudents: " + String.join(", ", team.getStudentIDs()));

                Button viewSubmissionsButton = new Button("📂 View Submissions");
                viewSubmissionsButton.setOnAction(e -> handleViewSubmissions(team.getTeamID()));

                teamBox.getChildren().addAll(teamInfo, viewSubmissionsButton);
                layout.getChildren().add(teamBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void handleViewSubmissions(String teamID) {
        List<PhaseSubmission> submissions = submissionService.getSubmissionsByTeamID(teamID);

        Stage submissionsStage = new Stage();
        submissionsStage.initModality(Modality.APPLICATION_MODAL);
        submissionsStage.setTitle("Submissions for Team: " + teamID);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_LEFT);

        if (submissions.isEmpty()) {
            layout.getChildren().add(new Label("No submissions found for this team."));
        } else {
            for (PhaseSubmission submission : submissions) {
                VBox submissionBox = new VBox(8);
                submissionBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");

                Label submissionInfo = new Label("📌 Phase: " + submission.getPhase() +
                                                "\n🗓 Date: " + submission.getSubmissionDate() +
                                                "\n📄 Status: " + submission.getStatus());

                // ⬇ Download Button
                Button downloadButton = new Button("⬇ Download as PDF");
                downloadButton.setOnAction(e -> onDownloadButtonClick(submission.getDocumentID()));

                // 📝 Feedback Button
                Button feedbackButton = new Button("📝 Give Feedback");
                feedbackButton.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(submission.getFeedback() != null ? submission.getFeedback() : "");
                    dialog.setTitle("Give Feedback");
                    dialog.setHeaderText("Provide feedback for Phase " + submission.getPhase());
                    dialog.setContentText("Feedback:");

                    dialog.showAndWait().ifPresent(feedback -> {
                        submission.setFeedback(feedback);
                        submissionService.updateSubmission(submission); // Ensure your service has this method!
                        showAlert("✅ Success", "Feedback submitted!");
                    });
                });

                // ✅ Accept / ❌ Reject Buttons
                HBox decisionButtons = new HBox(10);
                Button acceptButton = new Button("✅ Accept");
                Button rejectButton = new Button("❌ Reject");

                acceptButton.setOnAction(e -> {
                    submission.setStatus("Approved");
                    submissionService.updateSubmission(submission);
                    showAlert("✅ Status Updated", "Submission marked as Approved.");
                    submissionsStage.close(); // Refreshing logic can be added
                });

                rejectButton.setOnAction(e -> {
                    submission.setStatus("Rejected");
                    submissionService.updateSubmission(submission);
                    showAlert("⚠ Status Updated", "Submission marked as Rejected.");
                    submissionsStage.close();
                });

                decisionButtons.getChildren().addAll(acceptButton, rejectButton);

                submissionBox.getChildren().addAll(submissionInfo, downloadButton, feedbackButton, decisionButtons);
                layout.getChildren().add(submissionBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 500);
        submissionsStage.setScene(scene);
        submissionsStage.showAndWait();
    }


    @FXML
    public void onDownloadButtonClick(String fileId) {
        String directoryPath = "downloaded_files";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("✅ Directory created: " + directoryPath);
            } else {
                showAlert("❌ Error", "Could not create directory for saving files.");
                return;
            }
        }

        String destinationPath = directoryPath + "/" + fileId + ".pdf";

        // Download the file as PDF (you need to handle the actual export in DriveUploader)
        DriveUploader.retrieveFile(fileId, destinationPath);

        showAlert("✅ Success", "File downloaded as PDF to:\n" + destinationPath);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            // Create FXMLLoader and set Spring's controller factory
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));
            loader.setControllerFactory(CapstoneApplication.getApplicationContext()::getBean);

            // Load the FXML
            Parent root = loader.load();
            
            // Get the current stage and set new scene
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();

            System.out.println("✅ Successfully logged out to homepage");
        } catch (IOException e) {
            System.out.println("❌ Error loading homepage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
