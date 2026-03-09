package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.spellwalker.spellwalker_desktop.ConexionApi;

public class MainAppController implements Initializable {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void handlerAcceder(ActionEvent event) {
        String usuario = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText().trim();
        if (ConexionApi.login(usuario, contrasena)) {
            try {
                MainApp.usuario.put("usuario", usuario);
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/crear_personaje-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMaximized(true);
                MainApp.configurarStage(stage);
                stage.show();

                Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
                stage1.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Alert alertContrasena = new Alert(Alert.AlertType.ERROR);
            alertContrasena.setTitle("Error");
            alertContrasena.setContentText("La contraseña introducida no es valida");
            alertContrasena.showAndWait();
        }

    }

    private void marcarError() {
        campoUsuario.setStyle("-fx-border-color: red;");
        campoContrasena.setStyle("-fx-border-color: red;");
    }

    private void crearPersonaje() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/crear_personaje-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.show();

            Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
            stage1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerCrearUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/registro-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.show();

            Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
            stage1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
