package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    public void handlerAcceder(ActionEvent ignoredEvent) {
        String usuario = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText().trim();

        if (ConexionApi.login(usuario, contrasena)) {

            MainApp.usuario.put("usuario", usuario);
            SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/crear_personaje-view.fxml");

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("La contraseña introducida no es válida");
            alert.showAndWait();
        }
    }

    @FXML
    public void handlerCrearUsuario(ActionEvent event) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/registro-view.fxml");
    }
}