package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DetallePersonajeController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private Label lblCampana;
    @FXML private ComboBox<String> cbEscuelas;
    @FXML private ListView<String> lvEscuelas;
    @FXML private ComboBox<SpellItem> cbHechizos;
    @FXML private TableView<Hechizo> tvHechizos;
    @FXML private TableColumn<Hechizo, String> colNombre;
    @FXML private TableColumn<Hechizo, Integer> colCosteAp;
    @FXML private TableColumn<Hechizo, Integer> colCosteMana;
    @FXML private TableColumn<Hechizo, String> colTipo;
    @FXML private TableColumn<Hechizo, String> colVelocidad;
    @FXML private TableColumn<Hechizo, String> colLegendario;
    @FXML private TableColumn<Hechizo, String> colFusion;
    @FXML private TextArea taDescripcion;

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
            lvEscuelas.getItems().setAll(
                    ConexionApi.obtenerEscuelasDePersonaje(personaje.getId())
            );

            tvHechizos.getItems().setAll(
                    ConexionApi.obtenerHechizosDePersonaje(personaje.getId())
            );

            taDescripcion.setText(
                    ConexionApi.obtenerDescripcionPersonaje(personaje.getId())
            );

            cbEscuelas.getItems().setAll(ConexionApi.obtenerTodasLasEscuelas());
            
            List<SpellItem> hechizosAgrupados = ConexionApi.obtenerHechizosAgrupadosPorEscuela();
            SpellComboBoxHelper.setupSpellComboBox(cbHechizos);
            cbHechizos.getItems().setAll(hechizosAgrupados);

        } catch (IOException e) {
            mostrarError("Error al cargar datos del personaje" + e.getMessage());
        }
    }
    @FXML
    public void handlerAnadirEscuela(ActionEvent ignoredEvent) {
        String seleccionada = cbEscuelas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) return;

        if (lvEscuelas.getItems().contains(seleccionada)) {
            mostrarError("El personaje ya pertenece a esta escuela.");
            return;
        }

        try {
            int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(seleccionada);
            ConexionApi.vincularPersonajeAEscuela(personaje.getId(), idEscuela);
            lvEscuelas.getItems().add(seleccionada);

        } catch (IOException e) {
            mostrarError("Error al añadir escuela" + e.getMessage());
        }
    }

    @FXML
    public void handlerEliminarEscuela(ActionEvent ignoredEvent) {
        String seleccionada = lvEscuelas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        try {
            int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(seleccionada);
            ConexionApi.desvincularEscuelaDePersonaje(personaje.getId(), idEscuela);
            lvEscuelas.getItems().remove(seleccionada);

        } catch (IOException e) {
            mostrarError("Error al eliminar escuela"+e.getMessage());
        }
    }
    @FXML
    public void handlerAnadirHechizo(ActionEvent ignoredEvent) {
        SpellItem seleccionado = cbHechizos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        boolean yaExiste = tvHechizos.getItems().stream()
                .anyMatch(h -> h.getNombre().equals(seleccionado.getName()));

        if (yaExiste) {
            mostrarError("El personaje ya conoce este hechizo.");
            return;
        }

        try {
            Hechizo h = ConexionApi.obtenerHechizoPorNombre(seleccionado.getName());
            if (h != null) {
                ConexionApi.vincularHechizoAPersonaje(personaje.getId(), h.getId());
                tvHechizos.getItems().add(h);
            }
        } catch (IOException e) {
            mostrarError("Error al añadir hechizo" + e.getMessage());
        }
    }

    @FXML
    public void handlerEliminarHechizo(ActionEvent ignoreEvent) {
        Hechizo h = tvHechizos.getSelectionModel().getSelectedItem();
        if (h == null) return;

        try {
            ConexionApi.desvincularHechizoDePersonaje(personaje.getId(), h.getId());
            tvHechizos.getItems().remove(h);

        } catch (IOException e) {
            mostrarError("Error al eliminar hechizo"+ e.getMessage());
        }
    }

    @FXML
    public void handlerIrACrearHechizo(ActionEvent ignoredEvent) {
        SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/crear_hechizo-view.fxml", true);
    }

    @FXML
    public void handlerGuardar(ActionEvent ignoredEvent) {
        try {
            ConexionApi.actualizarDescripcionPersonaje(
                    personaje.getId(),
                    taDescripcion.getText()
            );

            mostrarExito("Descripción guardada correctamente");

        } catch (IOException e) {
            mostrarError("Error al guardar descripción"+ e.getMessage());
        }
    }


    @FXML
    public void handlerBorrarPersonaje(ActionEvent ignoredEvent) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Borrado");
        dialog.setHeaderText("Introduzca su contraseña:");

        ButtonType btnConfirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        PasswordField pf = new PasswordField();
        HBox box = new HBox(pf);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        pf.setOnAction(e -> {
            Button okButton = (Button) dialog.getDialogPane().lookupButton(btnConfirmar);
            if (okButton != null) okButton.fire();
        });

        dialog.setResultConverter(button -> button == btnConfirmar ? pf.getText() : null);

        dialog.showAndWait().ifPresent(password -> {

            try {
                String username = (String) MainApp.usuario.get("usuario");

                if (!ConexionApi.login(username, password)) {
                    mostrarError("Contraseña incorrecta.");
                    return;
                }

                if (ConexionApi.eliminarPersonaje(personaje.getId())) {
                    mostrarExito("Personaje eliminado correctamente.");
                    SceneManager.goBack();
                } else {
                    mostrarError("No se pudo eliminar el personaje.");
                }

            } catch (Exception e) {
                mostrarError("Error al conectar con el servidor."+ e.getMessage());
            }
        });
    }
    @FXML
    public void handlerVolver(ActionEvent ignoredEvent) {
        SceneManager.goBack();
    }
    private void mostrarExito(String msg) {
        mostrar(Alert.AlertType.INFORMATION, "Éxito", msg);
    }

    private void mostrarError(String msg) {
        mostrar(Alert.AlertType.ERROR, "Error", msg);
    }

    private void mostrar(Alert.AlertType type, String titulo, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}