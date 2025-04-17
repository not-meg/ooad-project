package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.TeamService;
import com.capstone.service.DriveUploader;
import com.capstone.service.PhaseSubmissionService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.util.Random;
import java.io.File;
import java.util.List;
import javafx.scene.layout.Region;
import java.util.Comparator;

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
            facultyLabel.setText(team.getFacultyID());
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
                System.out.println("Navigating to Dashboard..");
                System.out.println("Navigating to Dashboard...");

                // Close the sidebar if open
                if (isSidebarOpen) {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
                    transition.setToX(-200);
                    transition.play();
                    isSidebarOpen = false;
                }

                // Reload dashboard details using stored student ID
                if (loggedInStudentID != null) {
                    loadTeamDetails();
                } else {
                    System.out.println("⚠️ Cannot reload dashboard — student not logged in.");
                }
                break;
            case "Profile":
                System.out.println("Navigating to Profile... (TODO: Implement navigation)");
                break;
            case "Notification":
                System.out.println("Navigating to Notification... (TODO: Implement navigation)");
                showTeamStatusNotification();
                break;
            case "Submission":
                System.out.println("Opening Submissions popup...");
                handleSubmissions();
                break;
            case "Mentor Feedback":
                System.out.println("Navigating to Mentor Feedback...");
                handleMentorFeedback();
                break;
            case "Results":
                System.out.println("Navigating to Results... (TODO: Implement navigation)");
                break;
            case "Review Schedule":
                System.out.println("Navigating to Review Schedule... (TODO: Implement navigation)");
                break;
            case "Submit to Conference":
                System.out.println("Navigating to Conference... (TODO: Implement navigation)");
                handleConference();
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

            // Build the team details string
            StringBuilder details = new StringBuilder();
            details.append("Team ID: ").append(team.getTeamID()).append("\n");
            details.append("Problem Statement: ").append(team.getProblemStatement()).append("\n");
            details.append("Faculty ID: ").append(team.getFacultyID()).append("\n");
            details.append("Student IDs:\n");

            for (String member : team.getStudentIDs()) {
                details.append("- ").append(member).append("\n");
            }

            // Show it in an Alert
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
        if (teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Team service not initialized or student not logged in.");
            return;
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found! Please contact support.");
            return;
        }

        Team team = teamOpt.get();

        if (!"Accepted".equalsIgnoreCase(team.getStatus())) {
            showAlert("Access Denied", "Submissions are only allowed for approved teams.");
            return;
        }

        showSubmissionPopup();
    }

    private void showSubmissionPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("📎 Submit Project");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📤 Upload Submission");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label phaseLabel = new Label("Select Phase:");
        ComboBox<String> phaseComboBox = new ComboBox<>();
        phaseComboBox.getItems().addAll("Abstract", "Report", "Presentation", "Final Code");
        phaseComboBox.setPromptText("Choose a phase");

        Label fileLabel = new Label("📁 No file selected.");
        Button uploadButton = new Button("Choose File");
        uploadButton.setOnAction(e -> handleFileUpload());

        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();

        submitButton.setOnAction(e -> {
            String selectedPhase = phaseComboBox.getValue();
            String filePath = fileLabel.getText();
            handleSubmission(selectedPhase, filePath, statusLabel);
        });

        VBox phaseSection = new VBox(5, phaseLabel, phaseComboBox);
        VBox uploadSection = new VBox(5, uploadButton, fileLabel);
        VBox submitSection = new VBox(10, submitButton, statusLabel);

        root.getChildren().addAll(titleLabel, phaseSection, uploadSection, submitSection);
    
        Scene scene = new Scene(root, 450, 400);
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

    private void handleSubmission(String selectedPhase, String fileLabelText, Label statusLabel) {
        if (selectedFile == null || selectedPhase == null) {
            statusLabel.setText("Please select both a file and a phase.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
    
        // Fetch team ID
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            statusLabel.setText("Team not found. Cannot submit.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
    
        String teamId = teamOpt.get().getTeamID();
        int selectedPhaseInt = mapPhaseToInt(selectedPhase);
    
        if (selectedPhaseInt < 1 || selectedPhaseInt > 4) {
            statusLabel.setText("Invalid phase selected.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
    
        // Get existing submissions
        List<PhaseSubmission> existingSubs = submissionService.getSubmissionsByTeamID(teamId);
        existingSubs.sort(Comparator.comparingInt(PhaseSubmission::getPhase)); // Ascending sort
    
        if (!existingSubs.isEmpty()) {
            PhaseSubmission lastSubmission = existingSubs.get(existingSubs.size() - 1);
            int lastPhase = lastSubmission.getPhase();
            String lastStatus = lastSubmission.getStatus();
    
            if ("Rejected".equalsIgnoreCase(lastStatus)) {
                // If rejected, must re-submit the same phase
                if (selectedPhaseInt != lastPhase) {
                    statusLabel.setText("You must re-submit Phase " + lastPhase + " before proceeding.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
            } else {
                // If last was accepted
                if (lastPhase == 4) {
                    statusLabel.setText("✅ All 4 phases have already been successfully submitted!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    return;
                }
                if (selectedPhaseInt != lastPhase + 1) {
                    statusLabel.setText("You must complete Phase " + (lastPhase + 1) + " first.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
            }
        } else {
            // No previous submissions yet, only allow Phase 1
            if (selectedPhaseInt != 1) {
                statusLabel.setText("You must start with Phase 1.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }
    
        // Upload file to Drive
        String fileId = DriveUploader.uploadFile(selectedFile);
    
        if (fileId != null) {
            PhaseSubmission phaseSubmission = new PhaseSubmission(
                teamId,
                selectedPhaseInt,
                fileId
            );
    
            boolean dbSuccess = submissionService.saveOrUpdateSubmission(phaseSubmission);
    
            if (dbSuccess) {
                statusLabel.setText("✅ Successfully uploaded for phase: " + selectedPhase);
                statusLabel.setStyle("-fx-text-fill: green;");
                simulateAIChecksAndDisplay(statusLabel, selectedPhase, teamId, selectedPhaseInt);
            } else {
                statusLabel.setText("❌ Failed to save to the database.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            statusLabel.setText("❌ Upload failed. Please try again.");
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

    private void simulateAIChecksAndDisplay(Label statusLabel, String selectedPhase, String teamId, int phaseInt) {
        statusLabel.setText(statusLabel.getText() + "\n\n🔍 Running AI and plagiarism checks...");
    
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            Random rand = new Random();
            int aiScore = rand.nextInt(18); // 0 to 30
            int plagiarismScore = rand.nextInt(18); // 0 to 30
    
            StringBuilder result = new StringBuilder();
            result.append("\n\n✅ Analysis for ").append(selectedPhase).append(" complete:")
                  .append("\n💡 AI Detection Score: ").append(aiScore).append("%")
                  .append("\n📄 Plagiarism Score: ").append(plagiarismScore).append("%");
    
            // Fetch the latest submission (assuming only one per teamId+phase combo)
            PhaseSubmission latest = submissionService.getSubmissionByTeamIDAndPhase(teamId, phaseInt);
    
            if (latest != null) {
                if (aiScore > 15 || plagiarismScore > 15) {
                    latest.setStatus("Rejected");
                    result.append("\n\n❌ Submission Rejected: High AI or Plagiarism score detected.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                } else {
                    latest.setStatus("AI Check Passed");
                    result.append("\n\n🎉 Submission Accepted: Passed all checks.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                }
    
                submissionService.updateSubmission(latest); // Save updated status
            } else {
                result.append("\n⚠️ Could not update submission status (not found).");
            }
    
            statusLabel.setText(statusLabel.getText() + result);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    } 

    @FXML
    private void showTeamStatusNotification() {
        if (teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Team service or student ID is not initialized.");
            return;
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            String teamStatus = team.getStatus(); // Get the status of the team

            // Create and display the alert with team status
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Team Status");
            alert.setHeaderText("Your Team Status");
            alert.setContentText("The current status of your team is: " + teamStatus);
            alert.showAndWait();
        } else {
            showAlert("Team Not Found", "You are not part of any team.");
        }
    }
    
    @FXML
    private void handleMentorFeedback() {
        if (submissionService == null || teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Required services not initialized.");
            return;
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found. Please contact support.");
            return;
        }

        String teamId = teamOpt.get().getTeamID();
        List<PhaseSubmission> submissions = submissionService.getSubmissionsByTeamID(teamId);

        if (submissions == null || submissions.isEmpty()) {
            showAlert("No Submissions", "Your team hasn't submitted anything yet.");
            return;
        }

        StringBuilder submissionDetails = new StringBuilder();
        submissionDetails.append("Team ID: ").append(teamId).append("\n\n");

        for (PhaseSubmission sub : submissions) {
            submissionDetails.append("📌 Phase: ").append(mapIntToPhase(sub.getPhase())).append("\n");
            submissionDetails.append("📁 File ID: ").append(sub.getDocumentID()).append("\n");
            submissionDetails.append("💬 Faculty Guide Feedback: ").append(sub.getFeedback()).append("\n");
            submissionDetails.append("📄 Submission Status: ").append(sub.getStatus()).append("\n");
            submissionDetails.append("----------------------------\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Your Team's Submissions");
        alert.setHeaderText("Here's what your team has submitted:");
        alert.setContentText(submissionDetails.toString());
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // so large text fits
        alert.showAndWait();
    }

    private String mapIntToPhase(int phase) {
        switch (phase) {
            case 1:
                return "Abstract";
            case 2:
                return "Report";
            case 3:
                return "Presentation";
            case 4:
                return "Final Code";
            default:
                return "Unknown Phase";
        }
    }

    @FXML
    private void handleConference() {
        if (teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Team service not initialized or student not logged in.");
            return;
        }
    
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found! Please contact support.");
            return;
        }
    
        Team team = teamOpt.get();
    
        if (!"Accepted".equalsIgnoreCase(team.getStatus())) {
            showAlert("Access Denied", "Only accepted teams can submit to the conference.");
            return;
        }
    
        showConferencePopup(team.getTeamID());
    }
    
    private void showConferencePopup(String teamId) {
        Stage popupStage = new Stage();
        popupStage.setTitle("📢 Conference Submission");
    
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);
    
        Label titleLabel = new Label("📤 Upload Conference Material");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    
        Label fileLabel = new Label("📁 No file selected.");
        Button uploadButton = new Button("Choose File");
        uploadButton.setOnAction(e -> {
            handleFileUpload();
            if (selectedFile != null) {
                fileLabel.setText("📁 " + selectedFile.getName());
            }
        });
    
        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();
    
        submitButton.setOnAction(e -> {
            if (selectedFile == null) {
                statusLabel.setText("❌ Please select a file before submitting.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
    
            // Upload to Drive
            String fileId = DriveUploader.uploadFile(selectedFile);
    
            if (fileId != null) {
                // You can optionally store this info using a ConferenceSubmissionService or just log it for now
                statusLabel.setText("✅ Conference submission uploaded successfully!\n📋 File ID: " + fileId);
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("❌ Upload failed. Please try again.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });
    
        root.getChildren().addAll(titleLabel, uploadButton, fileLabel, submitButton, statusLabel);
    
        Scene scene = new Scene(root, 450, 350);
        popupStage.setScene(scene);
        popupStage.show();
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