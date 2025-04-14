package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.TeamService;
import com.capstone.service.DriveUploader;
import com.capstone.service.PhaseSubmissionService;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.Optional;
import java.io.File;

public class DashboardController {

    @FXML
    private Label teamIDLabel;
    @FXML
    private Label problemStatementLabel;
    @FXML
    private Label facultyLabel;
    @FXML
    private VBox sidebar;

    @FXML
    private Label dashboardLink;
    @FXML
    private Label profileLink;
    @FXML
    private Label settingsLink;

    private boolean isSidebarOpen = false;

    // Placeholder team details (Replace with actual backend data later)
    private String teamID;
    private String problemStatement;
    private String facultyName;

    private TeamService teamService;

    public DashboardController() {
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    private PhaseSubmissionService submissionService;

    public void setSubmissionService(PhaseSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    private String loggedInStudentID;
    private File selectedFile;

    public void setLoggedInStudentID(String studentID) {
        this.loggedInStudentID = studentID;
        loadTeamDetails(); // Load data once ID is set
    }

    @FXML
    public void initialize() {
        teamIDLabel.setText(teamID);
        problemStatementLabel.setText(problemStatement);
        facultyLabel.setText(facultyName);

        // onDownloadButtonClick("1LEPejTJ8WtSiC-Jj-6iROg7Jk7VccL-g");
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
                System.out.println("Opening Submissions popup...");
                handleSubmissions();
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
    private void handleSubmissions() {
        System.out.println("🚀 Opening Submissions popup...");

        Stage popupStage = new Stage();
        popupStage.setTitle("📎 Submit Project");

        // === Root Layout ===
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        // === Title ===
        Label titleLabel = new Label("📤 Upload Submission");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // === Phase Selection ===
        Label phaseLabel = new Label("Select Phase:");
        ComboBox<String> phaseComboBox = new ComboBox<>();
        phaseComboBox.getItems().addAll("Abstract", "Report", "Presentation", "Final Code");
        phaseComboBox.setPromptText("Choose a phase");

        // === File Upload Section ===
        Label fileLabel = new Label("📁 No file selected.");
        Button uploadButton = new Button("Choose File");
        uploadButton.setOnAction(e -> handleFileUpload());

        // === Submit Button and Status ===
        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();

        submitButton.setOnAction(e -> {
            String selectedPhase = phaseComboBox.getValue();
            String filePath = fileLabel.getText();
            handleSubmission(selectedPhase, filePath, statusLabel);
        });

        // === Layout Composition ===
        VBox phaseSection = new VBox(5, phaseLabel, phaseComboBox);
        VBox uploadSection = new VBox(5, uploadButton, fileLabel);
        VBox submitSection = new VBox(10, submitButton, statusLabel);

        root.getChildren().addAll(titleLabel, phaseSection, uploadSection, submitSection);

        // === Scene and Stage ===
        Scene scene = new Scene(root, 450, 350);
        popupStage.setScene(scene);
        popupStage.show();
    }

    // Handle file upload
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("File selected: " + selectedFile.getName());
        }
    }

    // Handle submission logic
    private void handleSubmission(String selectedPhase, String fileLabelText, Label statusLabel) {
        if (selectedFile == null || selectedPhase == null) {
            statusLabel.setText("Please select both a file and a phase.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Upload file to Google Drive
        String fileId = DriveUploader.uploadFile(selectedFile);

        if (fileId != null) {
            // File uploaded successfully, now save the submission in MongoDB
            PhaseSubmission phaseSubmission = new PhaseSubmission(
                    "T001", // Replace with actual team ID
                    mapPhaseToInt(selectedPhase),
                    fileId);

            // Save the submission to the database using PhaseSubmissionService
            boolean dbSuccess = submissionService.saveSubmission(phaseSubmission);

            if (dbSuccess) {
                statusLabel.setText("Successfully uploaded for phase: " + selectedPhase);
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("Failed to save to the database.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            statusLabel.setText("Upload failed. Please try again.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private int mapPhaseToInt(String phase) {
        switch (phase) {
            case "Abstract":
                return 1;
            case "Report":
                return 2;
            case "Presentation":
                return 3;
            case "Final Code":
                return 4;
            default:
                return 0;
        }
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
        DriveUploader.retrieveFile(fileId, destinationPath);

        showAlert("✅ Success", "File downloaded successfully.");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}