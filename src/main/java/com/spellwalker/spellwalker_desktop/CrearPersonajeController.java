package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CrearPersonajeController implements Initializable {

    @FXML
    private TextField campoNombre;
    @FXML
    private ComboBox<String> comboCampana;
    @FXML
    private ComboBox<String> comboEscuela;
    @FXML
    private ComboBox<String> comboSpell1;
    @FXML
    private ComboBox<String> comboSpell2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            comboEscuela.getItems().addAll(ConexionApi.obtenerTodasLasEscuelas());
            String currentUser = (String) MainApp.usuario.get("usuario");
            comboCampana.getItems().addAll(ConexionApi.obtenerCampanasDeUsuarioStr(currentUser));

            List<String> hechizos = ConexionApi.obtenerTodosNombresHechizos();
            comboSpell1.getItems().addAll(hechizos);
            comboSpell2.getItems().addAll(hechizos);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handlerNuevaCampana(ActionEvent ignoredEvent) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/crear_campana-view.fxml", true);
    }

    @FXML
    public void handlerCampanasGuardadas(ActionEvent ignoredEvent) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/campanas_guardadas-view.fxml", true);
    }

    @FXML
    public void handlerCrear(ActionEvent ignoredEvent) {
        String nombrePersonaje = campoNombre.getText();
        String nombreCampana = comboCampana.getValue();
        String nombreEscuela = comboEscuela.getValue();
        String spell1 = comboSpell1.getValue();
        String spell2 = comboSpell2.getValue();

        if (nombrePersonaje == null || nombrePersonaje.isBlank() ||
                nombreCampana == null || nombreEscuela == null ||
                spell1 == null || spell2 == null) {

            mostrarError("Por favor, rellena todos los campos.");
            return;
        }

        String usuarioActual = (String) MainApp.usuario.get("usuario");

        boolean creado = ConexionApi.crearPersonajeConNombreYCampana(
                nombrePersonaje, nombreCampana, usuarioActual);

        if (!creado) {
            mostrarError("No se pudo crear el personaje.");
            return;
        }

        try {
            int idPersonaje = ConexionApi.obtenerIdPersonajePorNombre(nombrePersonaje);
            int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(nombreEscuela);

            if (idPersonaje != -1 && idEscuela != -1) {

                boolean vinculado = ConexionApi.vincularPersonajeAEscuela(idPersonaje, idEscuela);
                boolean spell1Ins = ConexionApi.insertarSpellAPersonaje(nombrePersonaje, spell1);
                boolean spell2Ins = ConexionApi.insertarSpellAPersonaje(nombrePersonaje, spell2);

                if (vinculado && spell1Ins && spell2Ins) {
                    mostrarInfo();

                    // Limpiar campos
                    campoNombre.clear();
                    comboCampana.getSelectionModel().clearSelection();
                    comboEscuela.getSelectionModel().clearSelection();
                    comboSpell1.getSelectionModel().clearSelection();
                    comboSpell2.getSelectionModel().clearSelection();

                } else {
                    mostrarWarning();
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handlerCerrarSesion(ActionEvent ignoredActionEvent) {
        MainApp.usuario.clear();

        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/login-view.fxml");
    }

    @FXML
    public void handlerPersonajesGuardados(ActionEvent ignoredActionEvent) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/personajes_guardados.fxml", true);
    }

    @FXML
    public void handlerIrACrearHechizo(ActionEvent ignoredEvent) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/crear_hechizo-view.fxml", true);
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setContentText("Personaje creado, vinculado y hechizos asignados.");
        alert.showAndWait();
    }

    private void mostrarWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setContentText("El personaje se creó, pero hubo un error al vincular la escuela o los hechizos.");
        alert.showAndWait();
    }
}