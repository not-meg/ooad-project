package com.capstone;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaFXApp extends Application {
    private ProgressBar progressBar;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Capstone Project");

        // Title Label
        Label titleLabel = new Label("🚀 Welcome to Capstone Project");
        titleLabel.setFont(Font.font("Arial", 26));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        titleLabel.setEffect(new DropShadow(5, Color.GRAY));

        // TextField for Project Name
        TextField projectNameField = new TextField();
        projectNameField.setPromptText("Enter your project name...");
        projectNameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        // Dropdown for Project Type
        ComboBox<String> projectTypeBox = new ComboBox<>();
        projectTypeBox.getItems().addAll("Software Development", "AI/ML", "Research Paper", "Web App", "Embedded Systems");
        projectTypeBox.setPromptText("Select project type");
        projectTypeBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        // Start Button
        Button startButton = createStyledButton("🚀 Start Project", "#27ae60", "#229954");
        startButton.setOnAction(e -> startProject(projectNameField, projectTypeBox));

        // Exit Button
        Button exitButton = createStyledButton("❌ Exit", "#e74c3c", "#c0392b");
        exitButton.setOnAction(e -> primaryStage.close());

        // Progress Bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #27ae60;");

        // Status Label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.web("#16a085"));

        // Layout
        VBox layout = new VBox(15, titleLabel, projectNameField, projectTypeBox, startButton, progressBar, statusLabel, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f7f7, #dfe6e9); -fx-padding: 30px;");

        // Scene
        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);

        // Apply fade-in animation
        applyFadeInEffect(layout);

        primaryStage.show();
    }

    // Handles project start with animations
    private void startProject(TextField projectNameField, ComboBox<String> projectTypeBox) {
        String projectName = projectNameField.getText();
        String projectType = projectTypeBox.getValue();

        if (projectName.isEmpty() || projectType == null) {
            statusLabel.setText("⚠️ Please enter a project name and select a type!");
            return;
        }

        statusLabel.setText("🚀 Starting project: " + projectName + " (" + projectType + ")...");
        progressBar.setProgress(0);

        // Progress animation
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(200);
                    final double progress = i / 100.0;
                    javafx.application.Platform.runLater(() -> progressBar.setProgress(progress));
                }
                javafx.application.Platform.runLater(() -> statusLabel.setText("✅ Project successfully started!"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Creates styled buttons with hover effects
    private Button createStyledButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 10;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 10;"));
        button.setEffect(new DropShadow(5, Color.GRAY));

        // Scale transition for click effect
        button.setOnMousePressed(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            scaleDown.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });

        return button;
    }

    // Fade-in animation on startup
    private void applyFadeInEffect(VBox layout) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), layout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
