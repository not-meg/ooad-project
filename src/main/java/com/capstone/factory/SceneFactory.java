package com.capstone.factory;

import javafx.event.ActionEvent;
import javafx.scene.Scene;

public interface SceneFactory {
    Scene createScene(ActionEvent event, String title);
}