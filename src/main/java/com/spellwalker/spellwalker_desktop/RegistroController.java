package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private PasswordField campoConfirmar;

    @FXML
    private TextField campoEmail;

    @FXML
    private javafx.scene.control.CheckBox checkNotificaciones;

    @FXML
    public void handlerCrearCuenta(ActionEvent event) {
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();
        String confirmar = campoConfirmar.getText();
        String email = campoEmail.getText();
        boolean recibeNotificaciones = checkNotificaciones.isSelected();

        try {

            if (usuario.isBlank() || contrasena.isBlank() ||
                    confirmar.isBlank() || email.isBlank()) {

                mostrarError("Uno o más campos están vacíos.");
                return;
            }

            if (!confirmar.equals(contrasena)) {
                mostrarError("Las contraseñas no coinciden.");
                return;
            }

            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                mostrarError("El email no es válido.");
                return;
            }

            if (ConexionApi.mailExiste(email)) {
                mostrarError("El email " + email + " ya está registrado.");
                return;
            }

            if (ConexionApi.usuarioExiste(usuario)) {
                mostrarError("El nombre de usuario " + usuario + " ya existe.");
                return;
            }

            ConexionApi.registerPerfil(usuario, contrasena, email);

            mostrarExito("Usuario registrado correctamente.");

            SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/login-view.fxml");

        } catch (Exception e) {
            mostrarError("Ha ocurrido un error: " + e.getMessage());
        }
    }

    @FXML
    public void handlerLogin(ActionEvent event) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/login-view.fxml");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}