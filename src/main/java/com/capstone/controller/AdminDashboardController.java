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
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableRow;
import javafx.geometry.Pos;
import javafx.util.Duration;

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

// Method to open the "Schedule Review" pop-up window
private void openScheduleReviewPopup(Team team) {
    Stage schedulePopup = new Stage();
    schedulePopup.setTitle("Schedule Review");

    VBox scheduleRoot = new VBox(10);
    scheduleRoot.setPadding(new Insets(20));

    // Team ID and Faculty ID labels
    Label teamIdLabel = new Label("Team ID: " + team.getTeamID());
    Label facultyIdLabel = new Label("Faculty ID: " + team.getFacultyID());

    // Center the labels and remove bold font style
    teamIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal; -fx-alignment: center;");
    facultyIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal; -fx-alignment: center;");

    // Add labels to the VBox
    scheduleRoot.getChildren().addAll(teamIdLabel, facultyIdLabel);

    // Set alignment of the labels in the VBox to center
    VBox.setMargin(teamIdLabel, new Insets(0, 0, 10, 0));
    VBox.setMargin(facultyIdLabel, new Insets(0, 0, 10, 0));

    // Center the VBox content
    scheduleRoot.setAlignment(Pos.CENTER);

    Scene scheduleScene = new Scene(scheduleRoot, 300, 200);
    schedulePopup.setScene(scheduleScene);
    schedulePopup.show();
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
