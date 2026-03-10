package com.spellwalker.spellwalker_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PersonajesCampanaController implements Initializable {

    @FXML
    private Label lblTitulo;

    @FXML
    private TableView<Personaje> tablaPersonajes;

    @FXML
    private TableColumn<Personaje, String> colNombre;

    @FXML
    private TableColumn<Personaje, String> colCreador;

    @FXML
    private TableColumn<Personaje, String> colCampana;

    @FXML
    private TableColumn<Personaje, String> colEscuela;

    @FXML
    private TableColumn<Personaje, String> colSpells;

    @FXML
    private TableColumn<Personaje, String> colDescripcion;

    private Campana campana;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombrePersonaje"));
        colCreador.setCellValueFactory(new PropertyValueFactory<>("creador"));
        colCampana.setCellValueFactory(new PropertyValueFactory<>("nombreCampana"));
        colEscuela.setCellValueFactory(new PropertyValueFactory<>("escuela"));
        colSpells.setCellValueFactory(new PropertyValueFactory<>("spells"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    public void setCampana(Campana campana) {
        this.campana = campana;
        if (campana != null) {
            lblTitulo.setText("Personajes de la Campaña: " + campana.getNombre());
            cargarPersonajes();
        }
    }

    private void cargarPersonajes() {
        try {
            List<PersonajesId> personajesId = ConexionApi.obtenerPersonajesDeCampana(campana.getIdCampana());

            for (PersonajesId p : personajesId) {
                String nombreEscuela = String.join(", ",
                        ConexionApi.obtenerEscuelasDePersonaje(Integer.parseInt(p.getId()))
                );

                String spellsTexto = String.join(", ",
                        ConexionApi.obtenerSpellsDePersonaje(Integer.parseInt(p.getId()))
                );

                tablaPersonajes.getItems().add(
                        new Personaje(
                                Integer.parseInt(p.getId()),
                                p.getNombre(),
                                campana.getNombre(),
                                nombreEscuela,
                                spellsTexto,
                                p.getDescripcion(),
                                p.getPerfil()
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerVolver(ActionEvent actionEvent) {
        SceneManager.goBack();
    }
}
