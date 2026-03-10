package com.spellwalker.spellwalker_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CampanasGuardadasController implements Initializable {

    @FXML
    private TableView<Campana> tablaCampanas;
    @FXML
    private TableColumn<Campana, String> colNombre;
    @FXML
    private TableColumn<Campana, String> colDescripcion;
    @FXML
    private TextField campoUsuario;

    private ObservableList<Campana> listaCampanas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        cargarCampanas();
        configurarDobleClick();
    }

    private void configurarDobleClick() {
        tablaCampanas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaCampanas.getSelectionModel().getSelectedItem() != null) {
                Campana seleccionada = tablaCampanas.getSelectionModel().getSelectedItem();
                PersonajesCampanaController controller = SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/personajes_campana-view.fxml", true);
                if (controller != null) {
                    controller.setCampana(seleccionada);
                }
            }
        });
    }

    private void cargarCampanas() {
        try {
            String usuarioActual = (String) MainApp.usuario.get("usuario");
            List<Campana> campanasList = ConexionApi.obtenerCampanasDeUsuarioObjeto(usuarioActual);

            listaCampanas = FXCollections.observableArrayList(campanasList);
            tablaCampanas.setItems(listaCampanas);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error al cargar las campañas.");
        }
    }

    @FXML
    public void handlerInvitar(ActionEvent event) {
        Campana campanaSeleccionada = tablaCampanas.getSelectionModel().getSelectedItem();
        if (campanaSeleccionada == null) {
            mostrarError("Por favor, selecciona una campaña de la tabla primero.");
            return;
        }

        String username = campoUsuario.getText();
        if (username == null || username.isBlank()) {
            mostrarError("Por favor, introduce el nombre del usuario a invitar.");
            return;
        }

        try {
            if (!ConexionApi.usuarioExiste(username)) {
                mostrarError("El usuario '" + username + "' no existe en el sistema.");
                return;
            }

            boolean vinculado = ConexionApi.vincularPerfilCampana(username, campanaSeleccionada.getIdCampana());
            if (vinculado) {
                mostrarInfo("El usuario " + username + " fue añadido a la campaña '" + campanaSeleccionada.getNombre()
                        + "'.");
                campoUsuario.clear();
            } else {
                mostrarError("No se pudo añadir al usuario; puede que ya esté en la campaña.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error inesperado al invitar al usuario.");
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
