package com.capstone.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

            case "reviewScheduleLink":
                System.out.println("Navigating to Review Schedule...");
                break;

            case "resultsLink":
                System.out.println("Navigating to Results...");
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

        // Create a TableView to display users
        TableView<User> tableView = new TableView<>();

        // Define columns
        TableColumn<User, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn);

        // Fetch users and set into TableView
        List<User> users = adminService.getAllUsers();
        tableView.getItems().addAll(users);

        // Layout
        VBox vbox = new VBox(tableView);
        vbox.setPadding(new Insets(10));

        // Scene and show
        Scene scene = new Scene(vbox, 600, 400);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with main window
        popupStage.showAndWait();
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out... (TODO)");
        // TODO: Implement actual logout logic and redirect to login page
    }
}
