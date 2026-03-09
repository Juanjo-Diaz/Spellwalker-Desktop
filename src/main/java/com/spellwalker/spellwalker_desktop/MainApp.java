package com.spellwalker.spellwalker_desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {
    public static Map<String, Object> usuario = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.init(stage);
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/login-view.fxml");
        stage.setMaximized(true);
        MainApp.configurarStage(stage);
        stage.setTitle("SpellWalker");
        stage.show();
    }

    public static void configurarStage(Stage stage) {
        if (stage == null)
            return;

        stage.getIcons().clear();
        stage.getIcons()
                .add(new Image(MainApp.class.getResourceAsStream("/com/spellwalker/spellwalker_desktop/spellwalker_title.ico")));

        if (stage.getScene() != null) {
            agregarManejadorF11(stage, stage.getScene());
        }

        stage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                agregarManejadorF11(stage, newScene);
            }
        });
    }

    private static void agregarManejadorF11(Stage stage, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                event.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}