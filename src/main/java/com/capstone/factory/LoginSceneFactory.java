package com.capstone.factory;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import com.capstone.controller.LoginController;
import com.capstone.service.AuthService;

public class LoginSceneFactory implements SceneFactory {
    private final ApplicationContext context;

    public LoginSceneFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Scene createScene(ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setAuthService(context.getBean(AuthService.class));
            controller.setApplicationContext(context);

            return new Scene(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}