package com.capstone;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class OOADProjectApplication extends Application {

    public static void main(String[] args) {
        SpringApplication.run(OOADProjectApplication.class, args);
        launch(args); // Starts JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }
}
