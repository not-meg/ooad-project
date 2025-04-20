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

import com.capstone.factory.SceneFactory;
import com.capstone.factory.LoginSceneFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HomepageController {
    private final ApplicationContext context;
    private final Map<String, SceneFactory> sceneFactories;

    public HomepageController(ApplicationContext context) {
        this.context = context;
        this.sceneFactories = new HashMap<>();
        initializeFactories();
    }

    private void initializeFactories() {
        sceneFactories.put("login", new LoginSceneFactory(context));
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        SceneFactory factory = sceneFactories.get("login");
        Scene scene = factory.createScene(event, "Login Page");
        
        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login Page");
            stage.show();
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
