package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CrearHechizoController implements Initializable {

    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoAp;
    @FXML
    private TextField campoMana;
    @FXML
    private ComboBox<String> comboTipo;
    @FXML
    private ComboBox<String> comboVelocidad;
    @FXML
    private ComboBox<String> comboEscuela;
    @FXML
    private CheckBox checkLegendario;
    @FXML
    private CheckBox checkFusion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            comboTipo.getItems().addAll("Cargable", "Canalizable", "Encadenable", "Normal");
            comboVelocidad.getItems().addAll("Rápido", "Normal", "Lento", "Rápida");

            List<String> escuelas = ConexionApi.obtenerTodasLasEscuelas();
            comboEscuela.getItems().addAll(escuelas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerCrear(ActionEvent event) {
        try {
            String nombre = campoNombre.getText();
            String apStr = campoAp.getText();
            String manaStr = campoMana.getText();
            String tipo = comboTipo.getValue();
            String velocidad = comboVelocidad.getValue();
            String escuelaNombre = comboEscuela.getValue();

            if (nombre == null || nombre.isBlank() || apStr == null || manaStr == null || tipo == null
                    || velocidad == null || escuelaNombre == null) {
                mostrarAlerta("Error", "Por favor, rellena todos los campos.", Alert.AlertType.ERROR);
                return;
            }

            int ap = Integer.parseInt(apStr);
            int mana = Integer.parseInt(manaStr);
            int legendario = checkLegendario.isSelected() ? 1 : 0;
            int fusion = checkFusion.isSelected() ? 1 : 0;
            int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(escuelaNombre);

            boolean creado = ConexionApi.crearHechizo(nombre, ap, mana, tipo, velocidad, legendario, idEscuela, fusion);

            if (creado) {
                mostrarAlerta("Éxito", "Hechizo creado correctamente.", Alert.AlertType.INFORMATION);
                handlerVolver(event);
            } else {
                mostrarAlerta("Error", "No se pudo crear el hechizo.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "AP y Maná deben ser números enteros.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error inesperado.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handlerVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/crear_personaje-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) campoNombre.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
