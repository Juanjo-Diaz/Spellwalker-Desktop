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

        boolean wasMaximized = mainStage.isMaximized();
        Scene previousScene = mainStage.getScene();

        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );

            Parent root = loader.load();
            Scene newScene = new Scene(root);

            if (saveHistory && previousScene != null)
                history.push(previousScene);

            if (wasMaximized)
                mainStage.setMaximized(false);

            mainStage.setScene(newScene);

            if (wasMaximized) {
                Platform.runLater(() ->
                        Platform.runLater(() ->
                                mainStage.setMaximized(true)
                        )
                );
            }

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            Scene previous = history.pop();

            boolean wasMaximized = mainStage.isMaximized();
            if (wasMaximized)
                mainStage.setMaximized(false);

            mainStage.setScene(previous);

            if (wasMaximized) {
                Platform.runLater(() ->
                        Platform.runLater(() ->
                                mainStage.setMaximized(true)
                        )
                );
            }
        }
    }

    public static Stage getStage() {
        return mainStage;
    }
}