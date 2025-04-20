package com.capstone.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableRow;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;
import java.util.Set;
import javafx.scene.control.TableCell;
import javafx.scene.text.Text;

import org.springframework.stereotype.Controller;
import com.capstone.service.AdminService;
import com.capstone.service.TeamService;
import com.capstone.model.Faculty;
import com.capstone.model.Student;
import com.capstone.model.Team;
import com.capstone.model.Admin;
import javafx.scene.control.ChoiceBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import com.capstone.CapstoneApplication;

@Controller
public class AdminDashboardController {

    @FXML
    private VBox sidebar;
    @FXML
    private Button menuButton;

    @FXML
    private Label adminIDLabel;
    @FXML
    private Label adminNameLabel;
    @FXML
    private Label adminEmailLabel;

    @FXML
    private Label homeLink;
    @FXML
    private Label usersLink;
    @FXML
    private Label reviewScheduleLink;
    @FXML
    private Label resultsLink;
    @FXML
    private Label logoutLink;
    @FXML
    private Label scheduleLink;


    @FXML
    private Button logoutButton;

    private boolean isSidebarOpen = false;
    private String loggedInAdminID;

    public AdminDashboardController() {
    }

    private AdminService adminService;
    private TeamService teamService;

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public void setLoggedInAdminID(String adminID) {
        this.loggedInAdminID = adminID;
        loadAdminDetails();
    }

    @FXML
    public void initialize() {
        System.out.println("AdminDashboardController initialized!");
    }

    private void loadAdminDetails() {
        if (adminService == null) {
            System.out.println("Error: adminService is null!");
            return;
        }

        if (loggedInAdminID == null) {
            System.out.println("Error: loggedInAdminID is null!");
            return;
        }

        adminService.getAdminById(loggedInAdminID).ifPresentOrElse(admin -> {
            adminIDLabel.setText(admin.getUserID());
            adminNameLabel.setText(admin.getName());
            adminEmailLabel.setText(admin.getEmail());
        }, () -> {
            adminIDLabel.setText("Unknown");
            adminNameLabel.setText("Unknown");
            adminEmailLabel.setText("Unknown");
        });
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
        String id = clickedLabel.getId();

        if (id == null) {
            return;
        }

        switch (id) {
            case "homeLink":
                System.out.println("Navigating to Home...");
                // Close the sidebar if open
                if (isSidebarOpen) {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
                    transition.setToX(-200);
                    transition.play();
                    isSidebarOpen = false;
                }

                // Reload dashboard details using stored student ID
                if (loggedInAdminID != null) {
                    loadAdminDetails();
                } else {
                    System.out.println("⚠️ Cannot reload dashboard — student not logged in.");
                }
                break;

            case "usersLink":
                System.out.println("Opening Manage Users popup...");
                handleManageUsers();
                break;

            case "teamsLink":
                System.out.println("Navigating to View Teams...");
                handleViewTeams();
                break;

            case "submissionsLink":
                System.out.println("Navigating to Submissions...");
                handleSubmissions();
                break;
            
            case "scheduleLink":
                System.out.println("Navigating to Schedule...");
                handleScheduleView();
                break;

            case "analyticsLink":
                System.out.println("Navigating to Analytics...");
                break;

            case "logoutLink":
                System.out.println("Logging out...");
                // Optionally call a logout handler here
                break;
            default:
                System.out.println("Unknown section clicked");
        }
    }

    @FXML
    private void handleManageUsers() {
        System.out.println("Opening Manage Users section...");

        if (adminService == null) {
            System.out.println("Error: adminService is not set!");
            return;
        }

        // Create a new Stage (popup window)
        Stage popupStage = new Stage();
        popupStage.setTitle("Manage Users");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // --- Table for Students ---
        Label studentLabel = new Label("Students");
        TableView<Student> studentTable = createStudentTable();
        studentTable.getItems().addAll(adminService.getAllStudents());

        // --- Table for Faculty ---
        Label facultyLabel = new Label("Faculty");
        TableView<Faculty> facultyTable = createFacultyTable();
        facultyTable.getItems().addAll(adminService.getAllFaculty());

        // --- Table for Admins ---
        Label adminLabel = new Label("Admins");
        TableView<Admin> adminTable = createAdminTable();
        adminTable.getItems().addAll(adminService.getAllAdmins());

        root.getChildren().addAll(
                studentLabel, studentTable,
                facultyLabel, facultyTable,
                adminLabel, adminTable);

        Scene scene = new Scene(new ScrollPane(root), 1000, 700); // Wider for more columns
        popupStage.setScene(scene);
        popupStage.show();
    }

    // Create Student Table (with department, gender, cgpa)
    private TableView<Student> createStudentTable() {
        TableView<Student> tableView = new TableView<>();

        TableColumn<Student, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Student, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Student, Double> cgpaColumn = new TableColumn<>("CGPA");
        cgpaColumn.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn, departmentColumn, genderColumn, cgpaColumn);
        return tableView;
    }

    // Create Faculty Table (with department, designation)
    private TableView<Faculty> createFacultyTable() {
        TableView<Faculty> tableView = new TableView<>();

        TableColumn<Faculty, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Faculty, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Faculty, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Faculty, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Faculty, String> designationColumn = new TableColumn<>("Designation");
        designationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn, departmentColumn, designationColumn);
        return tableView;
    }

    // Create Admin Table (basic user info)
    private TableView<Admin> createAdminTable() {
        TableView<Admin> tableView = new TableView<>();

        TableColumn<Admin, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<Admin, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Admin, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn);
        return tableView;
    }

    @FXML
    private void handleViewTeams() {
        if (teamService == null) {
            System.out.println("Error: teamService is not set!");
            return;
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("Teams");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TableView<Team> teamTable = new TableView<>();

        // Define columns
        TableColumn<Team, String> teamIdColumn = new TableColumn<>("Team ID");
        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("teamID"));
        teamIdColumn.setPrefWidth(150);

        TableColumn<Team, String> problemStatementColumn = new TableColumn<>("Problem Statement");
        problemStatementColumn.setCellValueFactory(new PropertyValueFactory<>("problemStatement"));
        problemStatementColumn.setPrefWidth(300);

        TableColumn<Team, String> facultyIdColumn = new TableColumn<>("Faculty ID");
        facultyIdColumn.setCellValueFactory(new PropertyValueFactory<>("facultyID"));
        facultyIdColumn.setPrefWidth(100);

        // Define studentIds column with multiline support
        TableColumn<Team, String> studentIdsColumn = new TableColumn<>("Student IDs");
        studentIdsColumn.setCellValueFactory(cellData -> {
            Set<String> studentIDs = cellData.getValue().getStudentIDs();
            String joinedStudentIDs = String.join("\n", studentIDs); // Use newlines
            return new javafx.beans.property.SimpleStringProperty(joinedStudentIDs);
        });
        studentIdsColumn.setPrefWidth(200);

        // Separate status column with dropdown
        TableColumn<Team, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(150);

        statusColumn.setCellFactory(param -> {
            return new TableCell<Team, String>() {
                private final ChoiceBox<String> statusChoiceBox = new ChoiceBox<>();

                {
                    statusChoiceBox.getItems().addAll("Pending", "Accepted", "Rejected");
                    statusChoiceBox.setOnAction(e -> {
                        Team team = getTableView().getItems().get(getIndex());
                        String newStatus = statusChoiceBox.getValue();
                        team.setStatus(newStatus);
                        teamService.updateTeamStatus(team); // Update status in backend
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        statusChoiceBox.setValue(item);
                        setGraphic(statusChoiceBox);
                    }
                }
            };
        });

        // Custom cell for multiline student IDs
        studentIdsColumn.setCellFactory(column -> {
            return new TableCell<Team, String>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(studentIdsColumn.widthProperty().subtract(10)); // Allow wrapping
                    setGraphic(text);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        text.setText(null);
                    } else {
                        text.setText(item);
                    }
                }
            };
        });

        // Add columns to the table
        teamTable.getColumns().addAll(teamIdColumn, problemStatementColumn, facultyIdColumn, studentIdsColumn,
                statusColumn);

        // Populate the table
        List<Team> teams = teamService.getAllTeams();
        teamTable.getItems().addAll(teams);

        root.getChildren().add(teamTable);

        Scene scene = new Scene(root, 1000, 700);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void handleScheduleView() {
    if (teamService == null) {
        System.out.println("Error: teamService is not set!");
        return;
    }

    Stage popupStage = new Stage();
    popupStage.setTitle("All Teams");

    VBox root = new VBox(10);
    root.setPadding(new Insets(10));

    TableView<Team> teamTable = new TableView<>();

    // Team ID Column
    TableColumn<Team, String> teamIdCol = new TableColumn<>("Team ID");
    teamIdCol.setCellValueFactory(new PropertyValueFactory<>("teamID"));
    teamIdCol.setPrefWidth(150);

    // Faculty ID Column
    TableColumn<Team, String> facultyIdCol = new TableColumn<>("Faculty ID");
    facultyIdCol.setCellValueFactory(new PropertyValueFactory<>("facultyID"));
    facultyIdCol.setPrefWidth(150);

    // Status Column
    TableColumn<Team, String> statusCol = new TableColumn<>("Status");
    statusCol.setCellValueFactory(param -> {
        // Set default status as "Pending"
        return new SimpleStringProperty("Pending");
    });
    statusCol.setPrefWidth(150);
    
    // Adjust table width to ensure all columns fit properly
    teamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    // Add the columns to the table
    teamTable.getColumns().addAll(teamIdCol, facultyIdCol, statusCol);

    // Filter only teams with status "Accepted"
    List<Team> acceptedTeams = teamService.getAllTeams()
        .stream()
        .filter(team -> "Accepted".equalsIgnoreCase(team.getStatus()))
        .toList();

    teamTable.getItems().addAll(acceptedTeams);

    // Set each row to be clickable (button style)
    teamTable.setRowFactory(tv -> {
        TableRow<Team> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (!row.isEmpty()) {
                Team team = row.getItem();
                openScheduleReviewPopup(team);  // Open the popup on row click
            }
        });
        return row;
    });

    root.getChildren().add(teamTable);

    Scene scene = new Scene(root, 400, 600);
    popupStage.setScene(scene);
    popupStage.show();
}

private void openScheduleReviewPopup(Team team) {
    Stage schedulePopup = new Stage();
    schedulePopup.setTitle("Schedule Review");

    VBox scheduleRoot = new VBox(15);
    scheduleRoot.setPadding(new Insets(20));
    scheduleRoot.setAlignment(Pos.CENTER);

    // Team ID and Faculty ID labels
    Label teamIdLabel = new Label("Team ID: " + team.getTeamID());
    Label facultyIdLabel = new Label("Faculty ID: " + team.getFacultyID());
    teamIdLabel.setStyle("-fx-font-size: 16px;");
    facultyIdLabel.setStyle("-fx-font-size: 16px;");

    // Phase dropdown
    Label phaseLabel = new Label("Phase:");
    ComboBox<String> phaseComboBox = new ComboBox<>();
    phaseComboBox.getItems().addAll("Phase 1", "Phase 2", "Phase 3", "Phase 4");
    phaseComboBox.setPrefWidth(200);
    phaseComboBox.setPromptText("Select Phase");

    // Title input
    Label titleLabel = new Label("Title:");
    TextField titleField = new TextField();
    titleField.setPromptText("Enter Review Title");
    titleField.setPrefWidth(200);

    // Review Date picker
    Label dateLabel = new Label("Review Date:");
    DatePicker datePicker = new DatePicker();

    // Review Time picker (using ComboBox for hours and minutes)
    Label timeLabel = new Label("Review Time:");
    HBox timeBox = new HBox(10);
    timeBox.setAlignment(Pos.CENTER);
    ComboBox<String> hourCombo = new ComboBox<>();
    ComboBox<String> minuteCombo = new ComboBox<>();
    hourCombo.setPrefWidth(80);
    minuteCombo.setPrefWidth(80);
    hourCombo.setPromptText("Hour");
    minuteCombo.setPromptText("Minute");

    // Populate hours (00 to 23)
    for (int i = 0; i < 24; i++) {
        hourCombo.getItems().add(String.format("%02d", i));
    }

    // Populate minutes (00, 15, 30, 45)
    for (int i = 0; i < 60; i += 15) {
        minuteCombo.getItems().add(String.format("%02d", i));
    }

    timeBox.getChildren().addAll(hourCombo, minuteCombo);

    // Add everything to the form
    scheduleRoot.getChildren().addAll(
        teamIdLabel,
        facultyIdLabel,
        phaseLabel, phaseComboBox,
        titleLabel, titleField,
        dateLabel, datePicker,
        timeLabel, timeBox
    );

    Scene scheduleScene = new Scene(scheduleRoot, 350, 450);
    schedulePopup.setScene(scheduleScene);
    schedulePopup.show();
}

// Update the Command interface and implementation
private interface Command {
    void execute();
}

private class ViewSubmissionCommand implements Command {
    private final Team team;
    private final double plagiarismScore;
    private final Stage stage;

    public ViewSubmissionCommand(Team team, double plagiarismScore, Stage stage) {
        this.team = team;
        this.plagiarismScore = plagiarismScore;
        this.stage = stage;
    }

    @Override
    public void execute() {
        displaySubmissionDetails();
    }

    private void displaySubmissionDetails() {
        VBox submissionBox = new VBox(8);
        submissionBox.setPadding(new Insets(15));
        submissionBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 10;");

        // Style for plagiarism score based on threshold
        String scoreColor = plagiarismScore <= 10 ? "#2ecc71" : "#e74c3c";
        
        Label submissionInfo = new Label(
            "📌 Team ID: " + team.getTeamID() +
            "\n📊 Problem: " + team.getProblemStatement()
        );

        Label plagiarismLabel = new Label(
            String.format("🤖 Plagiarism Score: %.2f%%", plagiarismScore)
        );
        plagiarismLabel.setStyle("-fx-text-fill: " + scoreColor + "; -fx-font-weight: bold;");

        submissionBox.getChildren().addAll(submissionInfo, plagiarismLabel);
        Scene scene = new Scene(submissionBox, 400, 300);
        stage.setScene(scene);
    }
}

@FXML
private void handleSubmissions() {
    if (teamService == null) {
        System.out.println("Error: teamService is not set!");
        return;
    }

    Stage popupStage = new Stage();
    popupStage.setTitle("Conference Submissions");

    VBox layout = new VBox(15);
    layout.setPadding(new Insets(15));
    layout.setAlignment(Pos.TOP_CENTER);

    // Get only teams that have made conference submissions
    List<Team> teams = teamService.getAllTeams().stream()
        .filter(team -> hasConferenceSubmission(team))
        .toList();

    if (teams.isEmpty()) {
        layout.getChildren().add(new Label("No conference submissions found."));
    } else {
        for (Team team : teams) {
            VBox teamBox = createTeamSubmissionBox(team);
            layout.getChildren().add(teamBox);
        }
    }

    ScrollPane scrollPane = new ScrollPane(layout);
    scrollPane.setFitToWidth(true);

    Scene scene = new Scene(scrollPane, 500, 400);
    popupStage.setScene(scene);
    popupStage.show();
}


private boolean hasConferenceSubmission(Team team) {
    return team.getTeamID().equals("T001");
}

private VBox createTeamSubmissionBox(Team team) {
    VBox teamBox = createBaseTeamBox();
    double plagiarismScore = generatePlagiarismScore();
    
    Label teamInfo = createTeamInfoLabel(team, plagiarismScore);
    Button viewSubmissionsButton = createViewSubmissionsButton(team, plagiarismScore);

    teamBox.getChildren().addAll(teamInfo, viewSubmissionsButton);
    return teamBox;
}

private VBox createBaseTeamBox() {
    VBox teamBox = new VBox(5);
    teamBox.setStyle("-fx-border-color: gray; " +
                     "-fx-border-width: 1; " +
                     "-fx-padding: 10; " +
                     "-fx-background-color: #f8f8f8; " +
                     "-fx-background-radius: 5; " +
                     "-fx-border-radius: 5;");
    return teamBox;
}

// Update createTeamInfoLabel to handle colors properly
// Update the createTeamInfoLabel method
private Label createTeamInfoLabel(Team team, double plagiarismScore) {
    VBox container = new VBox(5);
    
    Label infoLabel = new Label(
        "Team ID: " + team.getTeamID() + 
        "\nProblem: AI-powered plagiarism detection" + 
        "\nStudents: PES1UG22EE004, PES1UG22CS002, PES1UG22CS001, PES1UG22EC003"
    );
    
    Label scoreLabel = new Label(
        String.format("Plagiarism Score: %.2f%%", plagiarismScore)
    );
    
    // Set color based on score threshold (green if <= 10%, red otherwise)
    String scoreColor = plagiarismScore <= 10 ? "#2ecc71" : "#e74c3c";
    scoreLabel.setStyle("-fx-text-fill: " + scoreColor + "; -fx-font-weight: bold;");
    
    container.getChildren().addAll(infoLabel, scoreLabel);
    
    Label combinedLabel = new Label();
    combinedLabel.setGraphic(container);
    
    return combinedLabel;
}

private double generatePlagiarismScore() {
   
    return 5.5; 
}

private Button createViewSubmissionsButton(Team team, double plagiarismScore) {
    Button viewSubmissionsButton = new Button("📂 View Submission Details");
    viewSubmissionsButton.setStyle("-fx-background-color: #3498db; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-padding: 5 10; " +
                                 "-fx-cursor: hand;");
    
    viewSubmissionsButton.setOnAction(e -> handleViewTeamSubmissions(team, plagiarismScore));
    
    // Add hover effect
    viewSubmissionsButton.setOnMouseEntered(e -> 
        viewSubmissionsButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 5 10;"));
    viewSubmissionsButton.setOnMouseExited(e -> 
        viewSubmissionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10;"));
    
    return viewSubmissionsButton;
}

private void handleViewTeamSubmissions(Team team, double plagiarismScore) {
    Stage submissionsStage = new Stage();
    submissionsStage.initModality(Modality.APPLICATION_MODAL);
    submissionsStage.setTitle("Team Submissions: " + team.getTeamID());

    Command viewCommand = new ViewSubmissionCommand(team, plagiarismScore, submissionsStage);
    viewCommand.execute();
    
    submissionsStage.show();
}

    @FXML
    private void handleLogout() {
        try {
            // Create FXMLLoader with Spring's controller factory
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));
            loader.setControllerFactory(CapstoneApplication.getApplicationContext()::getBean);
            
            // Load the FXML
            Parent root = loader.load();
            
            // Get the current stage from the logout button
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            
            // Create and set the new scene
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
