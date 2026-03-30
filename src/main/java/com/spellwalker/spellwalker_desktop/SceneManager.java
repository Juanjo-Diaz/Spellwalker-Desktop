package com.spellwalker.spellwalker_desktop;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class SceneManager {

    private static Stage mainStage;
    private static final Stack<Scene> history = new Stack<>();

    public static void init(Stage stage) {
        mainStage = stage;
    }

    public static <T> T switchTo(String fxmlPath) {
        return switchTo(fxmlPath, false);
    }

    public static <T> T switchTo(String fxmlPath, boolean saveHistory) {

        if (mainStage == null) {
            throw new IllegalStateException("SceneManager no fue inicializado.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );

            Parent root = loader.load();

            Scene previousScene = mainStage.getScene();
            Scene newScene;
            if (previousScene != null && previousScene.getWidth() > 0 && previousScene.getHeight() > 0) {
                newScene = new Scene(root, previousScene.getWidth(), previousScene.getHeight());
            } else {
                newScene = new Scene(root);
            }

            if (saveHistory && previousScene != null)
                history.push(previousScene);

            mainStage.setScene(newScene);

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            Scene previous = history.pop();
            mainStage.setScene(previous);
        }
    }

    public static Stage getStage() {
        return mainStage;
    }
}