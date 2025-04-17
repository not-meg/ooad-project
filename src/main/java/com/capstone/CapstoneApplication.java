package com.capstone;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CapstoneApplication extends Application {

    private static ApplicationContext applicationContext;
    private FXMLLoader fxmlLoader;

    @Override
    public void init() {
        applicationContext = SpringApplication.run(getClass());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        fxmlLoader = new FXMLLoader(getClass().getResource("/views/homepage.fxml"));

        // Let Spring inject dependencies in the controller
        fxmlLoader.setControllerFactory(applicationContext::getBean);

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Capstone Management System");
        primaryStage.show();
    }

    @Override
    public void stop() {
        ((ConfigurableApplicationContext) applicationContext).close();
        Platform.exit();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
