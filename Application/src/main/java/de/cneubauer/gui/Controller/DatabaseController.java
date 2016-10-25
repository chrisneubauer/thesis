package de.cneubauer.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Created by Christoph Neubauer on 04.10.2016.
 * Provides controls for searching the database in the UI
 */
public class DatabaseController extends GUIController {
    @FXML
    private DatePicker date;
    @FXML
    private TextField creditor;
    @FXML
    private TextField debitor;
    @FXML
    private TextField value;

    @FXML
    protected void searchInDatabase() {
        System.out.println("Set values: Date: " + this.date.getValue().toString() +
                ", Kreditor: " + this.creditor.getText() +
                ", Debitor: " + this.debitor.getText() +
                ", and value: " + this.value.getText());
    }
}
