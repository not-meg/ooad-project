package com.capstone;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapstoneApplication extends Application {

    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args);
        launch(args); // Standard JavaFX launch method
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Capstone Project");
        Label label = new Label("Welcome to JavaFX + Spring Boot!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
