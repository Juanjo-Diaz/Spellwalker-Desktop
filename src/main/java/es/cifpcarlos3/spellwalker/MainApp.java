package es.cifpcarlos3.spellwalker;

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

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        configurarStage(stage);
        stage.setTitle("SpellWalker");
        stage.show();
    }

    public static void configurarStage(Stage stage) {
        if (stage == null)
            return;

        stage.getIcons().clear();
        stage.getIcons()
                .add(new Image(MainApp.class.getResourceAsStream("/es/cifpcarlos3/spellwalker/spellwalker_title.ico")));

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