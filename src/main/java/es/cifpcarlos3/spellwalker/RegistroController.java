package es.cifpcarlos3.spellwalker;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;
import es.cifpcarlos3.spellwalker.ConexionApi;

public class RegistroController {
    private static final String cuentaUsuario = "spellwalkerttrpg@gmail.com";

    private static final String password = "wdnh pchd xlbu wyjg\n";

    private static String mailDestinatario = "";



    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private PasswordField campoConfirmar;

    @FXML
    private TextField campoEmail;

    @FXML
    private javafx.scene.control.CheckBox checkNotificaciones;

    @FXML
    public void handlerCrearCuenta(ActionEvent event) {
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();
        String confirmar = campoConfirmar.getText();
        String email = campoEmail.getText();
        boolean recibeNotificaciones = checkNotificaciones.isSelected();
        int boolNotificaciones;
        try {
            if (usuario.isBlank()||contrasena.isBlank()||confirmar.isBlank()||email.isBlank()) {
                Alert alertVacio = new Alert(Alert.AlertType.ERROR);
                alertVacio.setTitle("Error");
                alertVacio.setContentText("Uno o más campos están vacíos.");
                alertVacio.showAndWait();
            } else if (!confirmar.equals(contrasena)) {
                Alert alertContrasena = new Alert(Alert.AlertType.ERROR);
                alertContrasena.setTitle("Error");
                alertContrasena.setContentText("Las contraseñas no coinciden.");
                alertContrasena.showAndWait();
            } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                Alert alertEmail = new Alert(Alert.AlertType.ERROR);
                alertEmail.setTitle("Error");
                alertEmail.setContentText("El email no es válido.");
                alertEmail.showAndWait();
            }
            else {

                if (recibeNotificaciones) {
                    boolNotificaciones = 1;
                } else {
                    boolNotificaciones = 0;
                }
                mailDestinatario =email;


                System.out.println("Usuario: " + usuario);
                System.out.println("Contraseña: " + contrasena);
                System.out.println("Confirmar: " + confirmar);
                System.out.println("Email: " + email);
                System.out.println("Recibe notificaciones: " + boolNotificaciones);
                if(ConexionApi.mailExiste(email)){
                    Alert alertRepetido = new Alert(Alert.AlertType.ERROR);
                    alertRepetido.setTitle("Error");
                    alertRepetido.setContentText("El email "+email+" ya está registrado.");
                    alertRepetido.showAndWait();
                } else if (ConexionApi.usuarioExiste(usuario)) {
                    Alert alertRepetido = new Alert(Alert.AlertType.ERROR);
                    alertRepetido.setTitle("Error");
                    alertRepetido.setContentText("El nombre de usuario "+ usuario +" ya existe.");
                    alertRepetido.showAndWait();
                }
                else {
                    ConexionApi.registerPerfil(usuario, contrasena, email);

                    correoConfirmacion(recibeNotificaciones); //He extraido el metodo pq era un lio si no :p

                    Alert alertSucc = new javafx.scene.control.Alert(
                            Alert.AlertType.INFORMATION);
                    alertSucc.setTitle("Registro Completado");
                    alertSucc.setHeaderText(null);
                    alertSucc.setContentText("Usuario registrado correctamente");
                    alertSucc.showAndWait();
                    handlerLogin(event);
                }
            }

        } catch (Exception e) {
            Alert alert = new javafx.scene.control.Alert(
                    Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ha ocurrido un error, código: " + e.getMessage());
            alert.showAndWait();
        }


    }

    public void correoConfirmacion(boolean recibeNotificaciones){
        if(recibeNotificaciones) {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(cuentaUsuario, password);
                        }
                    });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(cuentaUsuario));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestinatario));

                message.setSubject("Registro de cuenta con este email.");
                message.setText("Le informamos que se ha creado una cuenta de Spellwalker usando su correo electrónico.\n¡Regístrese ahora con nosotros!");
                Transport.send(message);
                System.out.println("Mail enviado");
            } catch (MessagingException e) {
                System.err.println(e.getStackTrace());
                Alert alert = new javafx.scene.control.Alert(
                        Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Ha ocurrido un error, código: " + e.getMessage());
                alert.showAndWait();

            }
        }
    }
    @FXML
    public void handlerLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
            stage1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
