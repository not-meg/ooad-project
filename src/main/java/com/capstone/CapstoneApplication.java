package com.capstone;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapstoneApplication {
    public static void main(String[] args) {
        // Start Spring Boot in a separate thread
        Thread springThread = new Thread(() -> SpringApplication.run(CapstoneApplication.class, args));
        springThread.setDaemon(true);
        springThread.start();

        // Start JavaFX application
        Application.launch(JavaFXApp.class, args);
    }
}
