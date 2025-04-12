package com.capstone;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CapstoneApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;

    @Override
    public void init() {
        springContext = SpringApplication.run(CapstoneApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        fxmlLoader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));

        // Let Spring inject dependencies in the controller
        fxmlLoader.setControllerFactory(springContext::getBean);

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Capstone Management System");
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
