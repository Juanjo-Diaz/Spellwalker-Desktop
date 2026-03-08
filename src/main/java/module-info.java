module es.cifpcarlos3.spellwalker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires jakarta.mail;
    requires java.net.http;


    opens es.cifpcarlos3.spellwalker to javafx.fxml;
    exports es.cifpcarlos3.spellwalker;
}