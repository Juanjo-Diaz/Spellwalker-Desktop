package com.spellwalker.spellwalker_desktop;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class SpellComboBoxHelper {

    public static void setupSpellComboBox(ComboBox<SpellItem> comboBox) {
        Callback<ListView<SpellItem>, ListCell<SpellItem>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(SpellItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setDisable(false);
                } else {
                    setText(item.getName());
                    if (item.isHeader()) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: gray; -fx-padding: 5 0 5 10;");
                        setDisable(true);
                    } else {
                        setStyle("-fx-font-weight: normal; -fx-text-fill: black; -fx-padding: 2 0 2 20;");
                        setDisable(false);
                    }
                }
            }
        };

        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isHeader()) {
                comboBox.getSelectionModel().select(oldVal);
            }
        });
    }
}
