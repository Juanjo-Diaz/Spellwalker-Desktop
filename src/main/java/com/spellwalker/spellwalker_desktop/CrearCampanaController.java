package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CrearCampanaController {

    @FXML
    private TextField campoNombre;
    @FXML
    private TextArea campoDescripcion;

    @FXML
    public void handlerCrear(ActionEvent event) {
        String nombre = campoNombre.getText();
        String descripcion = campoDescripcion.getText();

        if (nombre == null || nombre.isBlank()) {
            mostrarError("Por favor, introduce al menos un nombre para la campaña.");
            return;
        }

        try {
            String usuarioActual = (String) MainApp.usuario.get("usuario");
            boolean creado = ConexionApi.crearNuevaCampana(nombre, descripcion, usuarioActual);

            if (creado) {
                mostrarInfo("Campaña creada correctamente.");
                SceneManager.goBack();
            } else {
                mostrarError("No se pudo crear la campaña. Quizás ya existe otra con el mismo nombre.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error al conectar con el servidor.");
        }
    }

    @FXML
    public void handlerVolver(ActionEvent event) {
        SceneManager.goBack();
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
