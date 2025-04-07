package com.capstone.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SubmissionController {

    @FXML private ComboBox<String> phaseComboBox;
    @FXML private Label fileLabel;
    @FXML private Label statusLabel;

    private File selectedFile;

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

        // TODO: Upload logic (backend connection / storage)

        statusLabel.setText("Successfully uploaded for phase: " + selectedPhase);
        statusLabel.setStyle("-fx-text-fill: green;");
    }
}
