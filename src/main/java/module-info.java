module com.spellwalker.spellwalker_desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires jakarta.mail;
    requires java.net.http;


    opens com.spellwalker.spellwalker_desktop to javafx.fxml;
    exports com.spellwalker.spellwalker_desktop;
}