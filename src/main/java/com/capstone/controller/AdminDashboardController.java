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
import javafx.util.Duration;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;

import org.springframework.stereotype.Controller;
import com.capstone.service.AdminService;
import com.capstone.model.Faculty;
import com.capstone.model.Student;
import com.capstone.model.Admin;
import com.capstone.model.User;

@Controller
public class AdminDashboardController {

    @FXML private VBox sidebar;
    @FXML private Button menuButton;

    @FXML private Label adminIDLabel;
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;
    
    @FXML private Label homeLink;
    @FXML private Label usersLink;
    @FXML private Label reviewScheduleLink;
    @FXML private Label resultsLink;
    @FXML private Label logoutLink;

    @FXML private Button logoutButton;

    private boolean isSidebarOpen = false;
    private String loggedInAdminID;

    public AdminDashboardController() {}

    private AdminService adminService;

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
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
                break;

            case "usersLink":
                System.out.println("Opening Manage Users popup...");
                handleManageUsers();
                break;

            case "teamsLink":
                System.out.println("Navigating to View Teams...");
                break;

            case "submissionsLink":
                System.out.println("Navigating to Submissions...");
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
            adminLabel, adminTable
        );
    
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
    private void handleLogout() {
        System.out.println("Logging out... (TODO)");
        // TODO: Implement actual logout logic and redirect to login page
    }
}