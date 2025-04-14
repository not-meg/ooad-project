package com.capstone.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class SpringFXMLLoader {

    private final ApplicationContext context;

    @Autowired
    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public Parent load(String fxmlPath) throws IOException {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            throw new IllegalArgumentException("FXML file not found at path: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setControllerFactory(context::getBean); // Let Spring inject dependencies
        return loader.load();
    }
}
