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
import java.util.Date;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;

public class DashboardController {

    @FXML private Label teamIDLabel;
    @FXML private Label problemStatementLabel;
    @FXML private Label facultyLabel;
    @FXML private VBox sidebar;
    @FXML private Label dashboardLink;
    @FXML private Label settingsLink;
    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;
    private String loggedInStudentID;
    private File selectedFile;

    private TeamService teamService;
    private NotificationService notificationService;
    private StudentGradeService studentGradeService;
    private PhaseSubmissionService submissionService;

    @Autowired private PhaseSubmissionController phaseSubmissionController;

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

    public void setSubmissionService(PhaseSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public void setLoggedInStudentID(String studentID) {
        this.loggedInStudentID = studentID;
        loadTeamDetails();
    }

    @FXML
    public void initialize() {
        loadTeamDetails();
    }

    private void loadTeamDetails() {
        if (loggedInStudentID == null) return;
        TeamCommand loadCommand = new LoadTeamDetailsCommand(
            teamService,
            loggedInStudentID,
            teamIDLabel,
            problemStatementLabel,
            facultyLabel
        );
        new TeamActionExecutor().executeCommand(loadCommand);
    }

    @FXML
    private void handleNavigation(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        String section = clickedLabel.getText();

        switch (section) {
            case "Home":
            if (isSidebarOpen) {
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
                transition.setToX(-200);
                transition.play();
                isSidebarOpen = false;
            }
            if (loggedInStudentID != null) {
                TeamCommand loadCommand = new LoadTeamDetailsCommand(
                    teamService,
                    loggedInStudentID,
                    teamIDLabel,
                    problemStatementLabel,
                    facultyLabel
                );
                new TeamActionExecutor().executeCommand(loadCommand);
            }
            break;
            case "Notification":
                showNotifications();
                break;
            case "Submission":
                handleSubmissions();
                break;
            case "Mentor Feedback":
                handleMentorFeedback();
                break;
            case "Results":
                showResultsPopup();
                break;
            case "Submit to Conference":
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
        TeamCommand showPopup = new ShowTeamPopupCommand(teamService, loggedInStudentID);
        TeamActionExecutor executor = new TeamActionExecutor();
        executor.executeCommand(showPopup);
    }
    

    @FXML
    private void handleSubmissions() {
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found!");
            return;
        }
        Team team = teamOpt.get();
        if (!"Accepted".equalsIgnoreCase(team.getStatus())) {
            showAlert("Access Denied", "Submissions are only allowed for approved teams.");
            return;
        }
        new SubmissionPopup(phaseSubmissionController, loggedInStudentID).show();
    }

    private void showNotifications() {
        if (teamService == null || loggedInStudentID == null) {
            showAlert("Error", "Team service or student not initialized.");
            return;
        }

        if (notificationService == null) {
            notificationService = CapstoneApplication.getApplicationContext().getBean(NotificationService.class);
        }

        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);

        Team team = teamOpt.get();
        new NotificationPopup(notificationService, team).show();
    }

    private void handleMentorFeedback() {
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found!");
            return;
        }
        Team team = teamOpt.get();
    
        // Get submissions for the team's phases
        List<PhaseSubmission> submissions = submissionService.getSubmissionsByTeamID(team.getTeamID());
    
        // Call the MentorFeedbackPopup to show the feedback
        MentorFeedbackPopup.show(submissionService, team.getTeamID(), submissions);
    }    
    
    private void showResultsPopup() {
        Optional<Team> teamOpt = teamService.getTeamByStudentID(loggedInStudentID);
        if (!teamOpt.isPresent()) {
            showAlert("Error", "Team not found!");
            return;
        }

        if (studentGradeService == null) {
            studentGradeService = CapstoneApplication.getApplicationContext().getBean(StudentGradeService.class);
        }
    
        String teamId = teamOpt.get().getTeamID();
        new ResultsPopup(studentGradeService, loggedInStudentID, teamId).show();
    }

    @FXML
    private void handleConference() {
        ConferenceSubmission ui = new ConferenceSubmission(teamService, loggedInStudentID);
        ui.launchConferenceSubmission();
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

// --- Command Interface --- //
interface TeamCommand {
    void execute();
}

class TeamActionExecutor {
    public void executeCommand(TeamCommand command) {
        command.execute();
    }
}


class LoadTeamDetailsCommand implements TeamCommand {
    private final TeamService teamService;
    private final String studentID;
    private final Label teamIDLabel, problemStatementLabel, facultyLabel;

    public LoadTeamDetailsCommand(TeamService teamService, String studentID,
                                   Label teamIDLabel, Label problemStatementLabel, Label facultyLabel) {
        this.teamService = teamService;
        this.studentID = studentID;
        this.teamIDLabel = teamIDLabel;
        this.problemStatementLabel = problemStatementLabel;
        this.facultyLabel = facultyLabel;
    }

    @Override
    public void execute() {
        Optional<Team> teamOpt = teamService.getTeamByStudentID(studentID);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            teamIDLabel.setText(team.getTeamID());
            problemStatementLabel.setText(team.getProblemStatement());
            facultyLabel.setText(team.getFacultyID());
        } else {
            teamIDLabel.setText("N/A");
            problemStatementLabel.setText("No problem assigned.");
            facultyLabel.setText("No faculty assigned.");
        }
    }
}

class ShowTeamPopupCommand implements TeamCommand {
    private final TeamService teamService;
    private final String studentID;

    public ShowTeamPopupCommand(TeamService service, String studentID) {
        this.teamService = service;
        this.studentID = studentID;
    }

    @Override
    public void execute() {
        Optional<Team> teamOpt = teamService.getTeamByStudentID(studentID);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            StringBuilder details = new StringBuilder();
            details.append("Team ID: ").append(team.getTeamID()).append("\n")
                   .append("Problem Statement: ").append(team.getProblemStatement()).append("\n")
                   .append("Faculty ID: ").append(team.getFacultyID()).append("\n")
                   .append("Student IDs:\n");
            for (String member : team.getStudentIDs()) {
                details.append("- ").append(member).append("\n");
            }
            alert.setTitle("Team Details");
            alert.setHeaderText("Here's your awesome team 👇");
            alert.setContentText(details.toString());
        } else {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Team Not Found");
            alert.setHeaderText(null);
            alert.setContentText("You are not part of any team yet.");
        }

        alert.showAndWait();
    }
}

class SubmissionPopup {
    private PhaseSubmissionController controller;
    private String studentID;
    private File selectedFile;

    public SubmissionPopup(PhaseSubmissionController controller, String studentID) {
        this.controller = controller;
        this.studentID = studentID;
    }

    public void show() {
        Stage popupStage = new Stage();
        popupStage.setTitle("📎 Submit Project");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📤 Upload Submission");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<String> phaseComboBox = new ComboBox<>();
        phaseComboBox.getItems().addAll("Abstract", "Report", "Presentation", "Final Code");
        phaseComboBox.setPromptText("Choose a phase");

        Label fileLabel = new Label("📁 No file selected.");
        Button uploadButton = new Button("Choose File");
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) fileLabel.setText("📁 " + selectedFile.getName());
        });

        Button submitButton = new Button("✅ Submit");
        Label statusLabel = new Label();
        submitButton.setOnAction(e -> handleSubmission(phaseComboBox.getValue(), selectedFile, statusLabel));

        root.getChildren().addAll(
            titleLabel,
            new VBox(5, new Label("Select Phase:"), phaseComboBox),
            new VBox(5, uploadButton, fileLabel),
            new VBox(10, submitButton, statusLabel)
        );

        popupStage.setScene(new Scene(root, 450, 400));
        popupStage.show();
    }

    private void handleSubmission(String selectedPhase, File file, Label statusLabel) {
        if (selectedPhase == null || file == null) {
            statusLabel.setText("Please select both a phase and a file.");
            return;
        }

        StringBuilder resultLog = new StringBuilder();
        String result = controller.submitPhase(studentID, file, selectedPhase, resultLog);
        statusLabel.setText(result != null ? result : resultLog.toString());
    }
}

class ResultsPopup {

    private final StudentGradeService studentGradeService;
    private final String studentId;
    private final String teamId;

    public ResultsPopup(StudentGradeService studentGradeService, String studentId, String teamId) {
        this.studentGradeService = studentGradeService;
        this.studentId = studentId;
        this.teamId = teamId;
    }

    public void show() {
        Stage popupStage = new Stage();
        popupStage.setTitle("📊 Academic Results");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("📚 Phase-wise Results");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label selectPhaseLabel = new Label("Select Phase:");
        ComboBox<Integer> phaseDropdown = new ComboBox<>();
        phaseDropdown.getItems().addAll(1, 2, 3, 4);
        phaseDropdown.setPromptText("Choose Phase");

        Label isa1Label = new Label("ISA 1: -");
        Label isa2Label = new Label("ISA 2: -");
        Label esaLabel = new Label("ESA  : -");

        VBox resultSection = new VBox(10, isa1Label, isa2Label, esaLabel);
        resultSection.setPadding(new Insets(10));
        resultSection.setStyle("-fx-border-color: #aaa; -fx-border-width: 1;");
        resultSection.setAlignment(Pos.CENTER_LEFT);

        phaseDropdown.setOnAction(e -> {
            Integer selectedPhase = phaseDropdown.getValue();
            if (selectedPhase != null && studentId != null && teamId != null) {
                studentGradeService.getGradeByStudentTeamPhase(studentId, teamId, selectedPhase)
                        .ifPresentOrElse(grade -> {
                            isa1Label.setText("ISA 1: " + grade.getIsa1Grade());
                            isa2Label.setText("ISA 2: " + grade.getIsa2Grade());
                            esaLabel.setText("ESA  : " + grade.getEsaGrade());
                        }, () -> {
                            isa1Label.setText("ISA 1: -");
                            isa2Label.setText("ISA 2: -");
                            esaLabel.setText("ESA  : -");
                        });
            }
        });

        root.getChildren().addAll(titleLabel, selectPhaseLabel, phaseDropdown, resultSection);
        popupStage.setScene(new Scene(root, 400, 300));
        popupStage.show();
    }
}

class NotificationPopup {

    private final NotificationService notificationService;
    private final Team team;

    public NotificationPopup(NotificationService notificationService, Team team) {
        this.notificationService = notificationService;
        this.team = team;
    }

    public void show() {
        List<Notification> notifications = notificationService.getNotificationsByTeamId(team.getTeamID());
        if ("Rejected".equalsIgnoreCase(team.getStatus())) {
            Notification rejectionNote = new Notification();
            rejectionNote.setTitle("Team Rejected");
            rejectionNote.setComments("Your team has been rejected.");
            rejectionNote.setCreated_at(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            rejectionNote.setTeamId(team.getTeamID());
            notifications.add(0, rejectionNote);
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("📢 Team Notifications");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🔔 Notifications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Notification> table = NotificationTableFactory.create(notifications);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        root.getChildren().addAll(title, table, closeButton);
        popupStage.setScene(new Scene(root, 850, 600));
        popupStage.show();
    }
}

class NotificationTableFactory {

    public static TableView<Notification> create(List<Notification> notifications) {
        TableView<Notification> table = new TableView<>();
        table.setPrefWidth(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Notification, Notification> column = new TableColumn<>("");
        column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        column.setCellFactory(col -> new NotificationTableCell());

        table.getColumns().add(column);
        table.getItems().addAll(notifications);
        return table;
    }
}

class NotificationTableCell extends TableCell<Notification, Notification> {

    private final VBox vbox = new VBox(5);
    private final Label titleLabel = new Label();
    private final Label commentLabel = new Label();
    private final Label dateLabel = new Label();
    private final Button container = new Button();

    public NotificationTableCell() {
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(titleLabel, commentLabel, dateLabel);

        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        commentLabel.setStyle("-fx-font-size: 13px;");
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: grey;");

        container.setStyle("-fx-background-color: transparent;");
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
            dateLabel.setText("Posted on: " + item.getCreated_at());
            container.setOnAction(e -> new NotificationDetailsPopup(item).show());
            setGraphic(container);
        }
    }
}

class NotificationDetailsPopup {

    private final Notification notification;

    public NotificationDetailsPopup(Notification notification) {
        this.notification = notification;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("📄 Notification Details");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(
                createLabel("📌 " + notification.getTitle(), 18, true),
                createLabel("📝 Comments: " + notification.getComments(), 13, false),
                createLabel("ℹ️ Additional Info: " + notification.getAdditional_info(), 13, false),
                createLabel("📅 Created At: " + notification.getCreated_at(), 11, false),
                createLabel("⏳ Expires At: " + notification.getExpire_at(), 11, false),
                new Button("Close") {{
                    setOnAction(e -> stage.close());
                }}
        );

        stage.setScene(new Scene(root, 500, 300));
        stage.show();
    }

    private Label createLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: " + fontSize + "px;" + (bold ? " -fx-font-weight: bold;" : ""));
        return label;
    }
}

class MentorFeedbackPopup {

    public static void show(PhaseSubmissionService submissionService, String teamId, List<PhaseSubmission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            showAlert("No Submissions", "Your team hasn't submitted anything yet.");
            return;
        }

        StringBuilder details = new StringBuilder("Team ID: ").append(teamId).append("\n\n");

        for (PhaseSubmission sub : submissions) {
            details.append("📌 Phase: ").append(mapIntToPhase(sub.getPhase())).append("\n")
                    .append("📁 File ID: ").append(sub.getDocumentID()).append("\n")
                    .append("💬 Faculty Guide Feedback: ").append(sub.getFeedback()).append("\n")
                    .append("📄 Submission Status: ").append(sub.getStatus()).append("\n")
                    .append("----------------------------\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Team's Submissions");
        alert.setHeaderText("Here's what your team has submitted:");
        alert.setContentText(details.toString());
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private static String mapIntToPhase(int phase) {
        return switch (phase) {
            case 1 -> "Abstract";
            case 2 -> "Report";
            case 3 -> "Presentation";
            case 4 -> "Final Code";
            default -> "Unknown Phase";
        };
    }

    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
class ConferenceSubmission {

    private final TeamService teamService;
    private final String loggedInStudentID;

    private File selectedFile;

    public ConferenceSubmission(TeamService teamService, String loggedInStudentID) {
        this.teamService = teamService;
        this.loggedInStudentID = loggedInStudentID;
    }

    public void launchConferenceSubmission() {
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
                showConferencePopup(teamId, selectedConference);
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
                statusLabel.setText("✅ Submission to " + conferenceName + " uploaded!\n📋 File ID: " + fileId);
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

    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        selectedFile = fileChooser.showOpenDialog(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}