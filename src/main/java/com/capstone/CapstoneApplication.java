package com.capstone;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapstoneApplication {
    public static void main(String[] args) {
        // Start Spring Boot in a separate thread
        Thread springThread = new Thread(() -> SpringApplication.run(CapstoneApplication.class, args));
        springThread.setDaemon(true);
        springThread.start();

        // Start JavaFX application on the correct thread
        Application.launch(LoginView.class, args);
    }
}
