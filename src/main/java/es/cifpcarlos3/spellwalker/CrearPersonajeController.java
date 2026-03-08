package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.util.Optional;

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
            List<String> escuelas = ConexionApi.obtenerTodasLasEscuelas();
            comboEscuela.getItems().addAll(escuelas);

            List<String> campanas = ConexionApi.obtenerTodasLasCampanas();
            comboCampana.getItems().addAll(campanas);

            List<String> hechizos = ConexionApi.obtenerTodosNombresHechizos();
            comboSpell1.getItems().addAll(hechizos);
            comboSpell2.getItems().addAll(hechizos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerNuevaCampana(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Campaña");
        dialog.setHeaderText("Crear una nueva campaña");
        dialog.setContentText("Nombre de la campaña:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (!nombre.isBlank()) {
                try {
                    boolean creada = ConexionApi.crearNuevaCampana(nombre);
                    if (creada) {
                        comboCampana.getItems().setAll(ConexionApi.obtenerTodasLasCampanas());
                        comboCampana.getSelectionModel().select(nombre);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("No se pudo crear la campaña. Quizás ya existe.");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void handlerCrear(ActionEvent event) {
        String nombrePersonaje = campoNombre.getText();
        String nombreCampana = comboCampana.getValue();
        String nombreEscuela = comboEscuela.getValue();
        String spell1 = comboSpell1.getValue();
        String spell2 = comboSpell2.getValue();

        if (nombrePersonaje == null || nombrePersonaje.isBlank() || nombreCampana == null || nombreEscuela == null
                || spell1 == null || spell2 == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Por favor, rellena todos los campos.");
            alert.showAndWait();
            return;
        }

        String usuarioActual = (String) MainApp.usuario.get("usuario");

        boolean creado = ConexionApi.crearPersonajeConNombreYCampana(nombrePersonaje, nombreCampana, usuarioActual);

        if (creado) {
            try {
                int idPersonaje = ConexionApi.obtenerIdPersonajePorNombre(nombrePersonaje);
                int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(nombreEscuela);

                if (idPersonaje != -1 && idEscuela != -1) {
                    boolean vinculado = ConexionApi.vincularPersonajeAEscuela(idPersonaje, idEscuela);

                    boolean spell1Ins = ConexionApi.insertarSpellAPersonaje(nombrePersonaje, spell1);
                    boolean spell2Ins = ConexionApi.insertarSpellAPersonaje(nombrePersonaje, spell2);

                    if (vinculado && spell1Ins && spell2Ins) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setContentText("Personaje creado, vinculado a la escuela y hechizos asignados.");
                        alert.showAndWait();

                        campoNombre.clear();
                        comboCampana.getSelectionModel().clearSelection();
                        comboEscuela.getSelectionModel().clearSelection();
                        comboSpell1.getSelectionModel().clearSelection();
                        comboSpell2.getSelectionModel().clearSelection();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Aviso");
                        alert.setContentText(
                                "El personaje se creó, pero hubo un error al vincular la escuela o los hechizos.");
                        alert.showAndWait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo crear el personaje.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handlerCerrarSesion(ActionEvent actionEvent) {
        try {
            MainApp.usuario.clear();
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.show();

            Stage currentStage = (Stage) campoNombre.getScene().getWindow();
            currentStage.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerPersonajesGuardados(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("personajes_guardados.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.show();

            Stage currentStage = (Stage) campoNombre.getScene().getWindow();
            currentStage.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerIrACrearHechizo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("crear_hechizo-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) campoNombre.getScene().getWindow();
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}