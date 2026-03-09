package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PersonajesGuardadosController implements Initializable {

    @FXML
    private TableView<Personaje> tablaPersonajes;

    @FXML
    private TableColumn<Personaje, String> colNombre;

    @FXML
    private TableColumn<Personaje, String> colCampana;

    @FXML
    private TableColumn<Personaje, String> colEscuela;

    @FXML
    private TableColumn<Personaje, String> colSpells;

    @FXML
    private TableColumn<Personaje, String> colDescripcion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombrePersonaje"));
        colCampana.setCellValueFactory(new PropertyValueFactory<>("nombreCampana"));
        colEscuela.setCellValueFactory(new PropertyValueFactory<>("escuela"));
        colSpells.setCellValueFactory(new PropertyValueFactory<>("spells"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        cargarPersonajes();
    }

    private void cargarPersonajes() {
        String usuario = (String) MainApp.usuario.get("usuario");

        try {
            List<PersonajesId> personajesId = ConexionApi.obtenerPersonajesDeUsuario(usuario);

            System.out.println("PERSONAJES ENCONTRADOS = " + personajesId.size());
            for (PersonajesId p : personajesId) {
                System.out.println(" - " + p.getId() + " | " + p.getNombre() + " | " + p.getIdCampana());
            }
            for (PersonajesId p : personajesId) {

                String nombreCampana = ConexionApi.obtenerNombreCampanaPorId(
                        Integer.parseInt(p.getIdCampana()));

                List<String> escuelas = ConexionApi.obtenerEscuelasDePersonaje(
                        Integer.parseInt(p.getId()));
                String nombreEscuela = String.join(", ", escuelas);

                List<String> spells = ConexionApi.obtenerSpellsDePersonaje(
                        Integer.parseInt(p.getId()));
                String spellsTexto = String.join(", ", spells);

                tablaPersonajes.getItems().add(
                        new Personaje(
                                Integer.parseInt(p.getId()),
                                p.getNombre(),
                                nombreCampana,
                                nombreEscuela,
                                spellsTexto,
                                p.getDescripcion()));
            }

            tablaPersonajes.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && tablaPersonajes.getSelectionModel().getSelectedItem() != null) {
                    Personaje seleccionado = tablaPersonajes.getSelectionModel().getSelectedItem();
                    abrirDetallePersonaje(seleccionado);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirDetallePersonaje(Personaje personaje) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/detalle_personaje-view.fxml"));
            Scene scene = new Scene(loader.load());

            DetallePersonajeController controller = loader.getController();
            controller.setPersonaje(personaje);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.setTitle("Detalle de Personaje: " + personaje.getNombrePersonaje());
            stage.show();

            Stage currentStage = (Stage) tablaPersonajes.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlerVolver(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/spellwalker/spellwalker_desktop/crear_personaje-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            MainApp.configurarStage(stage);
            stage.show();

            Stage currentStage = (Stage) tablaPersonajes.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
