package com.spellwalker.spellwalker_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

                String nombreCampana =
                        ConexionApi.obtenerNombreCampanaPorId(Integer.parseInt(p.getIdCampana()));

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
                                nombreCampana,
                                nombreEscuela,
                                spellsTexto,
                                p.getDescripcion()
                        )
                );
            }

            tablaPersonajes.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 &&
                        tablaPersonajes.getSelectionModel().getSelectedItem() != null) {

                    Personaje seleccionado = tablaPersonajes.getSelectionModel().getSelectedItem();
                    abrirDetallePersonaje(seleccionado);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirDetallePersonaje(Personaje personaje) {

        DetallePersonajeController controller =
                SceneManager.switchTo("/com/spellwalker/spellwalker_desktop/detalle_personaje-view.fxml", true);

        controller.setPersonaje(personaje);
    }

    @FXML
    public void handlerVolver(ActionEvent actionEvent) {
        SceneManager.goBack();
    }
}