package com.capstone.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.capstone.service.AuthService;

import java.io.IOException;

@Component
public class HomepageController {

    private final ApplicationContext context;

    // Inject Spring's ApplicationContext
    public HomepageController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
private void handleLoginButton(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        // Get the controller and inject dependencies
        LoginController controller = loader.getController();
        controller.setAuthService(context.getBean(AuthService.class)); // Inject AuthService
        controller.setApplicationContext(context); // Inject ApplicationContext

        // Set the new scene with the login screen
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        loadSceneWithSpring(event, "/views/register.fxml", "Register Page", RegisterController.class); // Set controller
                                                                                                       // for register
    }

    private <T> void loadSceneWithSpring(ActionEvent event, String fxmlPath, String title, Class<T> controllerClass) {
        try {
            // Load the FXML without the controller if it's null
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // If controllerClass is not null, get the controller bean from Spring and set
            // it
            if (controllerClass != null) {
                T controller = context.getBean(controllerClass);
                loader.setController(controller);
            }

            // Load the FXML
            Parent root = loader.load();

            // Get the current stage and set the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
