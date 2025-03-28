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
        loadSceneWithSpring(event, "/views/login.fxml", "Login Page", LoginController.class);
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        loadSceneWithSpring(event, "/views/register.fxml", "Register Page", RegisterController.class);
    }

    private <T> void loadSceneWithSpring(ActionEvent event, String fxmlPath, String title, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Get the controller bean from Spring
            T controller = context.getBean(controllerClass);
            loader.setController(controller);

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
