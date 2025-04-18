package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.TeamService;
import com.capstone.service.DriveUploader;
import com.capstone.service.PhaseSubmissionService;
import com.capstone.service.NotificationService;
import com.capstone.model.Notification;

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
import java.util.List;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import com.capstone.CapstoneApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.Date;

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
    private Label settingsLink;

    @FXML
    private Button logoutButton;  // Add this line

    private boolean isSidebarOpen = false;

    private String teamID;
    private String problemStatement;
    private String facultyName;

    private TeamService teamService;
    private NotificationService notificationService;

    public DashboardController() {}

    @Autowired
    private PhaseSubmissionController phaseSubmissionController;

    public void setPhaseSubmissionController(PhaseSubmissionController controller) {
        this.phaseSubmissionController = controller;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
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
            case "Notification":
                System.out.println("Navigating to Notification... ");
                //showTeamStatusNotification();
                showNotificationsPopup();
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
                System.out.println("Opening Results popup...");
                showResultsPopup();
                break;
            case "Submit to Conference":
                System.out.println("Navigating to Conference... (TODO: Implement navigation)");
                handleConference();
                break;
            case "Logout":
                System.out.println("Logging out...");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));
                    loader.setControllerFactory(CapstoneApplication.getApplicationContext()::getBean);
                    Parent root = loader.load();
                    
                    Stage stage = (Stage) clickedLabel.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Homepage");
                    stage.show();

                    System.out.println("✅ Successfully logged out to homepage");
                } catch (IOException e) {
                    System.out.println("❌ Error loading homepage: " + e.getMessage());
                    e.printStackTrace();
                }
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

        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            selectedFile = fileChooser.showOpenDialog(null);
        
            if (selectedFile != null) {
                fileLabel.setText("📁 " + selectedFile.getName());
            }
        });
        
        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();

        submitButton.setOnAction(e -> {
            String selectedPhase = phaseComboBox.getValue();
            handleSubmission(selectedPhase, selectedFile, statusLabel);
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

    private void handleSubmission(String selectedPhase, File file, Label statusLabel) {
        if (selectedPhase == null || file == null) {
            statusLabel.setText("Please select both a phase and a file.");
            return;
        }

        StringBuilder resultLog = new StringBuilder();
        String result = phaseSubmissionController.submitPhase(loggedInStudentID, file, selectedPhase, resultLog);

        if (result != null) {
            statusLabel.setText(result);
        } else {
            statusLabel.setText(resultLog.toString()); // show details of AI/plagiarism check
        }
    }

    private void showResultsPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("📊 Academic Results");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("📚 Phase-wise Results");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label selectPhaseLabel = new Label("Select Phase:");
        ComboBox<String> phaseDropdown = new ComboBox<>();
        phaseDropdown.getItems().addAll("Phase 1", "Phase 2", "Phase 3", "Phase 4");
        phaseDropdown.setPromptText("Choose Phase");

        Label isa1Label = new Label("ISA 1: -");
        Label isa2Label = new Label("ISA 2: -");
        Label esaLabel = new Label("ESA  : -");

        VBox resultSection = new VBox(10, isa1Label, isa2Label, esaLabel);
        resultSection.setPadding(new Insets(10));
        resultSection.setStyle("-fx-border-color: #aaa; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        resultSection.setAlignment(Pos.CENTER_LEFT);

        phaseDropdown.setOnAction(e -> {
            String selected = phaseDropdown.getValue();
            // You can fetch real data here using service call if needed
            switch (selected) {
                case "Phase 1":
                    isa1Label.setText("ISA 1: 12");
                    isa2Label.setText("ISA 2: 14");
                    esaLabel.setText("ESA  : 24");
                    break;
                case "Phase 2":
                    isa1Label.setText("ISA 1: 13");
                    isa2Label.setText("ISA 2: 15");
                    esaLabel.setText("ESA  : 26");
                    break;
                case "Phase 3":
                    isa1Label.setText("ISA 1: 11");
                    isa2Label.setText("ISA 2: 12");
                    esaLabel.setText("ESA  : 22");
                    break;
                case "Phase 4":
                    isa1Label.setText("ISA 1: 14");
                    isa2Label.setText("ISA 2: 15");
                    esaLabel.setText("ESA  : 28");
                    break;
                default:
                    isa1Label.setText("ISA 1: -");
                    isa2Label.setText("ISA 2: -");
                    esaLabel.setText("ESA  : -");
            }
        });

        root.getChildren().addAll(titleLabel, selectPhaseLabel, phaseDropdown, resultSection);

        Scene scene = new Scene(root, 400, 300);
        popupStage.setScene(scene);
        popupStage.show();
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

    private void showNotificationsPopup() {
        if (teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Team service or student not initialized.");
            return;
        }

        if (notificationService == null) {
            notificationService = CapstoneApplication.getApplicationContext().getBean(NotificationService.class);
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Team Not Found", "You are not assigned to any team yet.");
            return;
        }

        String teamId = teamOpt.get().getTeamID();
        List<Notification> notifications = notificationService.getNotificationsByTeamId(teamId);

        Stage popupStage = new Stage();
        popupStage.setTitle("📢 Team Notifications");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🔔 Notifications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Notification> table = new TableView<>();
        table.setPrefWidth(800);

        TableColumn<Notification, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Notification, String> commentsCol = new TableColumn<>("Comments");
        commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));
        commentsCol.setPrefWidth(300);

        TableColumn<Notification, String> createdCol = new TableColumn<>("Created At");
        createdCol.setCellValueFactory(cellData -> {
            Date createdAt = cellData.getValue().getCreated_at();
            String formatted = createdAt != null ? createdAt.toString() : "N/A";
            return new ReadOnlyStringWrapper(formatted);
        });
        createdCol.setPrefWidth(150);

        TableColumn<Notification, String> expireCol = new TableColumn<>("Expires At");
        expireCol.setCellValueFactory(cellData -> {
            Date expireAt = cellData.getValue().getExpire_at();
            String formatted = expireAt != null ? expireAt.toString() : "N/A";
            return new ReadOnlyStringWrapper(formatted);
        });
        expireCol.setPrefWidth(150);

        table.getColumns().addAll(titleCol, commentsCol, createdCol, expireCol);
        table.getItems().addAll(notifications);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        root.getChildren().addAll(title, table, closeButton);

        Scene scene = new Scene(root, 850, 500);
        popupStage.setScene(scene);
        popupStage.show();
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
    
        showConferenceSelector(team.getTeamID());
    }

    private void showConferenceSelector(String teamId) {
        Stage selectorStage = new Stage();
        selectorStage.setTitle("🎓 Select Conference");
    
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
    
        Label instructionLabel = new Label("📑 Choose a conference to submit to:");
        instructionLabel.setStyle("-fx-font-size: 16px;");
    
        ComboBox<String> conferenceDropdown = new ComboBox<>();
        // You can dynamically load this list from a service or DB
        conferenceDropdown.getItems().addAll(
            "ICML 2025", "NeurIPS 2025", "CVPR 2025", "ACL 2025"
        );
        conferenceDropdown.setPromptText("Select a conference");
    
        Button continueButton = new Button("📝 Continue");
        Label warningLabel = new Label();
    
        continueButton.setOnAction(e -> {
            String selectedConference = conferenceDropdown.getValue();
            if (selectedConference == null || selectedConference.isEmpty()) {
                warningLabel.setText("🚨 Please select a conference to proceed.");
                warningLabel.setStyle("-fx-text-fill: red;");
            } else {
                selectorStage.close();
                showConferencePopup(teamId, selectedConference); // pass selection forward!
            }
        });
    
        root.getChildren().addAll(instructionLabel, conferenceDropdown, continueButton, warningLabel);
        Scene scene = new Scene(root, 350, 250);
        selectorStage.setScene(scene);
        selectorStage.show();
    }    
    
    private void showConferencePopup(String teamId, String conferenceName) {
        Stage popupStage = new Stage();
        popupStage.setTitle("📢 Submit to " + conferenceName);
    
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);
    
        Label titleLabel = new Label("📤 Upload Material for " + conferenceName);
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
    
            String fileId = DriveUploader.uploadFile(selectedFile);
            if (fileId != null) {
                // Optionally log with ConferenceSubmissionService with conferenceName
                statusLabel.setText("✅ Submission to " + conferenceName + "uploaded!\n📋 File ID: " + fileId);
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
        try {
            // Create FXMLLoader with Spring's controller factory
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