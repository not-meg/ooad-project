package com.capstone.controller;

import com.capstone.service.PhaseSubmissionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SubmissionController {

    @FXML private ComboBox<String> phaseComboBox;
    @FXML private Label fileLabel;
    @FXML private Label statusLabel;

    private File selectedFile;
    private final PhaseSubmissionService submissionService = new PhaseSubmissionService();

    @FXML
    public void initialize() {
        phaseComboBox.getItems().addAll("Abstract", "Report", "Presentation", "Final Code");
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Submission File");
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            fileLabel.setText("Selected: " + selectedFile.getName());
        } else {
            fileLabel.setText("No file selected.");
        }
    }

    @FXML
    private void handleUpload() {
        String selectedPhase = phaseComboBox.getValue();

        if (selectedFile == null || selectedPhase == null) {
            statusLabel.setText("Please select both a file and a phase.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = submissionService.uploadSubmission(selectedPhase, selectedFile);

        if (success) {
            statusLabel.setText("Successfully uploaded for phase: " + selectedPhase);
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setText("Upload failed. Please try again.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/student_dashboard.fxml"));
            Parent root = loader.load();
    
            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Student Dashboard");
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to go back.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
}
