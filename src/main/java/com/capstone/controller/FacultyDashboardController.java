package com.capstone.controller;

import com.capstone.model.PhaseSubmission;
import com.capstone.model.Team;
import com.capstone.service.DriveUploader;
import com.capstone.service.FacultyService;
import com.capstone.service.PhaseSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import com.capstone.service.StudentGradeService;
import com.capstone.model.StudentGrade;
import com.capstone.CapstoneApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import javafx.scene.control.ComboBox;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.net.URL;
import java.io.OutputStream;
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
    @FXML private Label resultsLink;
    @FXML private Label logoutLink;

    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;

    private FacultyService facultyService;
    private PhaseSubmissionService submissionService;

    @Autowired
    private StudentGradeService studentGradeService;

    private String loggedInFacultyID;

    public FacultyDashboardController() {}

    public void setFacultyService(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    public void setPhaseSubmissionService(PhaseSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public void setStudentGradeService(StudentGradeService studentGradeService) {
        this.studentGradeService = studentGradeService;
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
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/faculty_dashboard.fxml"));
                    loader.setControllerFactory(CapstoneApplication.getApplicationContext()::getBean);

                    Parent root = loader.load();

                    FacultyDashboardController controller = loader.getController();
                    controller.setFacultyService(facultyService);
                    controller.setPhaseSubmissionService(submissionService);
                    controller.setLoggedInFacultyID(loggedInFacultyID);

                    Stage stage = (Stage) homeLink.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Faculty Dashboard");
                    stage.show();

                    System.out.println("✅ Reloaded Faculty Dashboard");
                } catch (IOException e) {
                    System.out.println("❌ Error reloading dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case "Teams":
                handleViewTeams();
                break;
            case "Review Schedule":
                System.out.println("Navigating to Review Schedule... (TODO)");
                break;
            case "Results":
                handleViewResults();
                System.out.println("Navigating to Results... ");
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

    private void handleViewResults() {
    if (facultyService == null) {
        System.out.println("Error: facultyService is null!");
        return;
    }

    List<Team> teams = facultyService.getTeamsByFacultyID(loggedInFacultyID);

    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Results - Teams & Members");

    VBox layout = new VBox(15);
    layout.setPadding(new Insets(15));
    layout.setAlignment(Pos.TOP_CENTER);

    // Phase selector
    Label phaseLabel = new Label("Select Phase:");
    ComboBox<Integer> phaseSelector = new ComboBox<>();
    phaseSelector.getItems().addAll(1, 2, 3, 4, 5); // Phase options
    phaseSelector.setValue(1); // default selection
    IntegerProperty selectedPhase = new SimpleIntegerProperty(phaseSelector.getValue());
    phaseSelector.setOnAction(e -> selectedPhase.set(phaseSelector.getValue()));

    layout.getChildren().addAll(phaseLabel, phaseSelector);

    if (teams.isEmpty()) {
        layout.getChildren().add(new Label("No teams assigned."));
    } else {
        for (Team team : teams) {
            VBox teamBox = new VBox(5);
            teamBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f2f2f2;");

            Label teamInfo = new Label("Team ID: " + team.getTeamID() +
                                       "\nProblem: " + team.getProblemStatement());

            Button toggleMembersButton = new Button("👥 Show Team Members");
            VBox membersBox = new VBox();
            membersBox.setVisible(false);

            for (String studentId : team.getStudentIDs()) {
                HBox studentRow = new HBox(10);
                studentRow.setAlignment(Pos.CENTER_LEFT);

                Label studentLabel = new Label("👤 " + studentId);

                Button uploadGradeButton = new Button("📥 Upload Grades");
                uploadGradeButton.setOnAction(event -> showGradeInputDialog(studentId, team.getTeamID(), selectedPhase.get()));

                studentRow.getChildren().addAll(studentLabel, uploadGradeButton);
                membersBox.getChildren().add(studentRow);
            }

            toggleMembersButton.setOnAction(e -> {
                boolean visible = membersBox.isVisible();
                membersBox.setVisible(!visible);
                toggleMembersButton.setText(visible ? "👥 Show Team Members" : "👥 Hide Team Members");
            });

            teamBox.getChildren().addAll(teamInfo, toggleMembersButton, membersBox);
            layout.getChildren().add(teamBox);
        }
    }

    ScrollPane scrollPane = new ScrollPane(layout);
    scrollPane.setFitToWidth(true);

    Scene scene = new Scene(scrollPane, 600, 600);
    popupStage.setScene(scene);
    popupStage.showAndWait();
}


    private void uploadGrade(String studentId, String teamId, int phase, String isa1, String isa2, String esa) {
    try {
        URL url = new URL("http://localhost:8080/api/grades/save");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Construct grade object as JSON
        String jsonInput = String.format(
            "{" +
                "\"studentId\":\"%s\"," +
                "\"teamId\":\"%s\"," +
                "\"phase\":%d," +
                "\"isa1Grade\":\"%s\"," +
                "\"isa2Grade\":\"%s\"," +
                "\"esaGrade\":\"%s\"" +
            "}",
            studentId, teamId, phase, isa1, isa2, esa
        );

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            showAlert("Success", "Grades uploaded successfully!");
        } else {
            showAlert("Error", "Failed to upload grades. HTTP " + responseCode);
        }

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Exception occurred while uploading grades: " + e.getMessage());
    }
}

private void showGradeInputDialog(String studentId, String teamId, int phase) {
    TextInputDialog isa1Dialog = new TextInputDialog();
    isa1Dialog.setTitle("ISA1 Grade");
    isa1Dialog.setHeaderText("Enter ISA1 grade for student " + studentId);
    String isa1 = isa1Dialog.showAndWait().orElse("");

    TextInputDialog isa2Dialog = new TextInputDialog();
    isa2Dialog.setTitle("ISA2 Grade");
    isa2Dialog.setHeaderText("Enter ISA2 grade for student " + studentId);
    String isa2 = isa2Dialog.showAndWait().orElse("");

    TextInputDialog esaDialog = new TextInputDialog();
    esaDialog.setTitle("ESA Grade");
    esaDialog.setHeaderText("Enter ESA grade for student " + studentId);
    String esa = esaDialog.showAndWait().orElse("");

    uploadGrade(studentId, teamId, phase, isa1, isa2, esa);
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
