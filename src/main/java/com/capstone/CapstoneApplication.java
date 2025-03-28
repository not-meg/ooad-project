package com.capstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CapstoneApplication extends Application {

    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args); // Start JavaFX UI
    }

    @Override
    public void init() {
        // Initialize Spring Boot
        springContext = SpringApplication.run(CapstoneApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));
        loader.setControllerFactory(springContext::getBean); // Let Spring manage controllers
        Parent root = loader.load();

        primaryStage.setTitle("Capstone Project Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close(); // Shutdown Spring Boot properly
    }
}
