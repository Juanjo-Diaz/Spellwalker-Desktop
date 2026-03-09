package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DetallePersonajeController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblCampana;
    @FXML
    private ComboBox<String> cbEscuelas;
    @FXML
    private ListView<String> lvEscuelas;
    @FXML
    private ComboBox<String> cbHechizos;
    @FXML
    private TableView<Hechizo> tvHechizos;
    @FXML
    private TableColumn<Hechizo, String> colNombre;
    @FXML
    private TableColumn<Hechizo, Integer> colCosteAp;
    @FXML
    private TableColumn<Hechizo, Integer> colCosteMana;
    @FXML
    private TableColumn<Hechizo, String> colTipo;
    @FXML
    private TableColumn<Hechizo, String> colVelocidad;
    @FXML
    private TableColumn<Hechizo, String> colLegendario;
    @FXML
    private TableColumn<Hechizo, String> colFusion;
    @FXML
    private TextArea taDescripcion;

    private Personaje personaje;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCosteAp.setCellValueFactory(new PropertyValueFactory<>("costeAp"));
        colCosteMana.setCellValueFactory(new PropertyValueFactory<>("costeMana"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colVelocidad.setCellValueFactory(new PropertyValueFactory<>("velocidad"));
        colLegendario.setCellValueFactory(new PropertyValueFactory<>("legendarioTexto"));
        colFusion.setCellValueFactory(new PropertyValueFactory<>("esFusionTexto"));
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
        lblTitulo.setText("Personaje: " + personaje.getNombrePersonaje());
        lblCampana.setText(personaje.getNombreCampana());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<String> escuelas = ConexionApi.obtenerEscuelasDePersonaje(personaje.getId());
            lvEscuelas.getItems().setAll(escuelas);

            List<Hechizo> spells = ConexionApi.obtenerHechizosDePersonaje(personaje.getId());
            tvHechizos.getItems().setAll(spells);

            String descripcion = ConexionApi.obtenerDescripcionPersonaje(personaje.getId());
            taDescripcion.setText(descripcion);

            cbEscuelas.getItems().setAll(ConexionApi.obtenerTodasLasEscuelas());
            cbHechizos.getItems().setAll(ConexionApi.obtenerTodosNombresHechizos());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar datos del personaje");
        }
    }

    @FXML
    public void handlerAnadirEscuela(ActionEvent event) {
        String seleccionada = cbEscuelas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {

            if (lvEscuelas.getItems().contains(seleccionada)) {
                mostrarError("El personaje ya pertenece a esta escuela.");
                return;
            }
            try {
                int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(seleccionada);
                ConexionApi.vincularPersonajeAEscuela(personaje.getId(), idEscuela);
                lvEscuelas.getItems().add(seleccionada);
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al añadir escuela");
            }
        }
    }

    @FXML
    public void handlerEliminarEscuela(ActionEvent event) {
        String seleccionada = lvEscuelas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(seleccionada);
                ConexionApi.desvincularEscuelaDePersonaje(personaje.getId(), idEscuela);
                lvEscuelas.getItems().remove(seleccionada);
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al eliminar escuela");
            }
        }
    }

    @FXML
    public void handlerAnadirHechizo(ActionEvent event) {
        String seleccionado = cbHechizos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Comprobar si ya existe en la tabla
            boolean yaExiste = tvHechizos.getItems().stream()
                    .anyMatch(h -> h.getNombre().equals(seleccionado));

            if (yaExiste) {
                mostrarError("El personaje ya conoce este hechizo.");
                return;
            }
            try {
                Hechizo h = ConexionApi.obtenerHechizoPorNombre(seleccionado);
                if (h != null) {
                    ConexionApi.vincularHechizoAPersonaje(personaje.getId(), h.getId());
                    tvHechizos.getItems().add(h);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al añadir hechizo");
            }
        }
    }

    @FXML
    public void handlerEliminarHechizo(ActionEvent event) {
        Hechizo seleccionado = tvHechizos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                ConexionApi.desvincularHechizoDePersonaje(personaje.getId(), seleccionado.getId());
                tvHechizos.getItems().remove(seleccionado);
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al eliminar hechizo");
            }
        }
    }

    @FXML
    public void handlerGuardar(ActionEvent event) {
        try {
            ConexionApi.actualizarDescripcionPersonaje(personaje.getId(), taDescripcion.getText());
            mostrarExito("Descripción guardada correctamente");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al guardar descripción");
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void handlerBorrarPersonaje(ActionEvent event) {
        // Pedir contraseña para confirmar
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Borrado");
        dialog.setHeaderText("Introduzca su contraseña para borrar el personaje:");

        ButtonType btnConfirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        PasswordField pf = new PasswordField();
        HBox box = new HBox(pf);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        // Permitir confirmar pulsando ENTER
        pf.setOnAction(e -> {
            Button okButton = (Button) dialog.getDialogPane().lookupButton(btnConfirmar);
            if (okButton != null)
                okButton.fire();
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnConfirmar) {
                return pf.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(password -> {
            try {
                String username = (String) MainApp.usuario.get("usuario");
                if (ConexionApi.login(username, password)) {
                    if (ConexionApi.eliminarPersonaje(personaje.getId())) {
                        mostrarExito("Personaje eliminado correctamente.");
                        handlerVolver(null);
                    } else {
                        mostrarError("No se pudo eliminar el personaje.");
                    }
                } else {
                    mostrarError("Contraseña incorrecta.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al conectar con el servidor.");
            }
        });
    }

    @FXML
    public void handlerVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/personajes_guardados.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Personajes Guardados");
            stage.show();

            Stage currentStage = (Stage) lblTitulo.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
