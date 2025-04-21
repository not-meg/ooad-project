package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.TeamService;
import com.capstone.service.DriveUploader;
import com.capstone.service.PhaseSubmissionService;
import com.capstone.service.StudentGradeService;
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
import java.time.LocalDateTime;
import java.time.ZoneId;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.Date;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.ContentDisplay;
import java.util.HashMap;
import java.util.Map;

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
    private Button logoutButton; // Add this line

    private boolean isSidebarOpen = false;

    private String teamID;
    private String problemStatement;
    private String facultyName;

    private TeamService teamService;
    private NotificationService notificationService;
    private StudentGradeService studentGradeService;

    public DashboardController() {
    }

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

    @Autowired
    public void setStudentGradeService(StudentGradeService studentGradeService) {
        this.studentGradeService = studentGradeService;
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
                // showTeamStatusNotification();
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
    ComboBox<Integer> phaseDropdown = new ComboBox<>();
    phaseDropdown.getItems().addAll(1, 2, 3, 4); // Use Integer for phase
    phaseDropdown.setPromptText("Choose Phase");

    Label isa1Label = new Label("ISA 1: -");
    Label isa2Label = new Label("ISA 2: -");
    Label esaLabel = new Label("ESA  : -");

    VBox resultSection = new VBox(10, isa1Label, isa2Label, esaLabel);
    resultSection.setPadding(new Insets(10));
    resultSection.setStyle("-fx-border-color: #aaa; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
    resultSection.setAlignment(Pos.CENTER_LEFT);


    if (studentGradeService == null) {
        studentGradeService = CapstoneApplication.getApplicationContext().getBean(StudentGradeService.class);
    }


    phaseDropdown.setOnAction(e -> {
        Integer selectedPhase = phaseDropdown.getValue();
        if (selectedPhase != null && loggedInStudentID != null && teamIDLabel.getText() != null) {
            studentGradeService.getGradeByStudentTeamPhase(loggedInStudentID, teamIDLabel.getText(), selectedPhase)
                    .ifPresentOrElse(grade -> {
                        isa1Label.setText("ISA 1: " + (grade.getIsa1Grade() != null ? grade.getIsa1Grade() : "-"));
                        isa2Label.setText("ISA 2: " + (grade.getIsa2Grade() != null ? grade.getIsa2Grade() : "-"));
                        esaLabel.setText("ESA  : " + (grade.getEsaGrade() != null ? grade.getEsaGrade() : "-"));
                    }, () -> {
                        isa1Label.setText("ISA 1: -");
                        isa2Label.setText("ISA 2: -");
                        esaLabel.setText("ESA  : -");
                    });
        } else {
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

        Team team = teamOpt.get();
        String teamId = team.getTeamID();
        List<Notification> notifications = notificationService.getNotificationsByTeamId(teamId);

        // ✅ Inject rejection message dynamically (frontend only)
        if ("Rejected".equalsIgnoreCase(team.getStatus())) {
            Notification rejectionNote = new Notification();
            rejectionNote.setTeamId(teamId);
            rejectionNote.setTitle("Team Rejected");
            rejectionNote.setComments("Your team has been rejected.");
            rejectionNote.setCreated_at(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())); // ✅

            notifications.add(0, rejectionNote); // Add to top
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("📢 Team Notifications");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🔔 Notifications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Notification> table = new TableView<>();
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Notification, Notification> mainColumn = new TableColumn<>("");
        mainColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

        mainColumn.setCellFactory(col -> new TableCell<>() {
            private final VBox vbox = new VBox(5);
            private final Label titleLabel = new Label();
            private final Label commentLabel = new Label();
            private final Label dateLabel = new Label();
            private final Button container = new Button();

            {
                vbox.setAlignment(Pos.CENTER_LEFT);
                vbox.setPadding(new Insets(10));
                vbox.getChildren().addAll(titleLabel, commentLabel, dateLabel);

                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                commentLabel.setStyle("-fx-font-size: 13px;");
                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: grey;");

                container.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                container.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                container.setGraphic(vbox);
            }

            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(item.getTitle());
                    commentLabel.setText(item.getComments());
                    dateLabel.setText("Posted on: " + item.getCreated_at().toString());

                    container.setOnAction(e -> showNotificationDetailsPopup(item));
                    setGraphic(container);
                }
            }
        });

        table.getColumns().add(mainColumn);
        table.getItems().addAll(notifications);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        root.getChildren().addAll(title, table, closeButton);

        Scene scene = new Scene(root, 850, 600);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showNotificationDetailsPopup(Notification notification) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("📄 Notification Details");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("📌 " + notification.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label commentLabel = new Label("📝 Comments: " + notification.getComments());
        commentLabel.setWrapText(true);

        Label infoLabel = new Label("ℹ️ Additional Info: " + notification.getAdditional_info());
        infoLabel.setWrapText(true);

        Label dateLabel = new Label("📅 Created At: " + notification.getCreated_at());
        Label expireLabel = new Label("⏳ Expires At: " + notification.getExpire_at());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> detailsStage.close());

        root.getChildren().addAll(titleLabel, commentLabel, infoLabel, dateLabel, expireLabel, closeButton);

        Scene scene = new Scene(root, 500, 300);
        detailsStage.setScene(scene);
        detailsStage.show();
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
        conferenceDropdown.getItems().addAll(
                "ICML 2025", "NeurIPS 2025", "CVPR 2025", "ACL 2025");
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

    // First, add these interfaces and classes
    private interface SubmissionBuilder {
        SubmissionBuilder setTeamId(String teamId);

        SubmissionBuilder setConferenceName(String name);

        SubmissionBuilder setSubmissionFile(File file);

        SubmissionBuilder upload();

        Map<String, Object> build();
    }

    private class ConferenceSubmissionDirector {
        private final SubmissionBuilder builder;

        public ConferenceSubmissionDirector(SubmissionBuilder builder) {
            this.builder = builder;
        }

        public Map<String, Object> constructSubmission(String teamId, String conferenceName, File file) {
            return builder.setTeamId(teamId)
                    .setConferenceName(conferenceName)
                    .setSubmissionFile(file)
                    .upload()
                    .build();
        }
    }

    private class ConferenceSubmissionBuilder implements SubmissionBuilder {
        private String teamId;
        private String conferenceName;
        private File submissionFile;
        private String fileId;
        private String status = "PENDING";

        @Override
        public SubmissionBuilder setTeamId(String teamId) {
            this.teamId = teamId;
            return this;
        }

        @Override
        public SubmissionBuilder setConferenceName(String name) {
            this.conferenceName = name;
            return this;
        }

        @Override
        public SubmissionBuilder setSubmissionFile(File file) {
            this.submissionFile = file;
            return this;
        }

        @Override
        public SubmissionBuilder upload() {
            if (this.submissionFile != null) {
                try {
                    this.fileId = DriveUploader.uploadFile(this.submissionFile);
                } catch (Exception e) {
                    System.out.println("❌ Upload failed: " + e.getMessage());
                    this.fileId = null;
                }
            }
            return this;
        }

        @Override
        public Map<String, Object> build() {
            validateSubmission();

            Map<String, Object> submission = new HashMap<>();
            submission.put("teamId", teamId);
            submission.put("conferenceName", conferenceName);
            submission.put("fileId", fileId);
            submission.put("status", status);
            return submission;
        }

        private void validateSubmission() {
            if (teamId == null || teamId.isEmpty()) {
                throw new IllegalStateException("Team ID is required");
            }
            if (conferenceName == null || conferenceName.isEmpty()) {
                throw new IllegalStateException("Conference name is required");
            }
            if (fileId == null) {
                throw new IllegalStateException("File upload failed or not attempted");
            }
        }
    }

    // Update the showConferencePopup method to use the Builder pattern
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
        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();

        SubmissionBuilder builder = new ConferenceSubmissionBuilder();
        ConferenceSubmissionDirector director = new ConferenceSubmissionDirector(builder);
        File[] selectedFile = new File[1]; // Array to hold file reference

        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            selectedFile[0] = fileChooser.showOpenDialog(popupStage);
            if (selectedFile[0] != null) {
                fileLabel.setText("📁 " + selectedFile[0].getName());
            }
        });

        submitButton.setOnAction(e -> {
            try {
                Map<String, Object> submission = director.constructSubmission(
                        teamId,
                        conferenceName,
                        selectedFile[0]);

                if (submission.get("fileId") != null) {
                    statusLabel.setText("✅ Submission successful!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    statusLabel.setText("❌ Upload failed. Please try again.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (Exception ex) {
                statusLabel.setText("❌ Error: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        root.getChildren().addAll(titleLabel, uploadButton, fileLabel, submitButton, statusLabel);
        Scene scene = new Scene(root, 450, 350);
        popupStage.setScene(scene);
        popupStage.show();
    }
}