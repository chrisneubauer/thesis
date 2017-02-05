package de.cneubauer.gui.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 04.10.2016.
 * Provides controls for searching the database in the UI
 */
public class DatabaseController extends GUIController {
    @FXML private DatePicker date;
    @FXML private TextField creditor;
    @FXML private TextField debitor;
    @FXML private TextField value;

    @FXML
    protected void searchInDatabase(ActionEvent e) {
        String logMsg = "Started searching for values: Date: ";
        if (this.date.getValue() != null) {
            logMsg += (this.date.getValue().toString());
        }
        logMsg += ", Kreditor: " + this.creditor.getText() +
                ", Debitor: " + this.debitor.getText() +
                ", and value: " + this.value.getText();
        Logger.getLogger(this.getClass()).log(Level.INFO, logMsg);
        this.openDatabaseResults(e);
    }

    //this method opens databaseResultsController after user selected filter criteria
    @FXML
    private void openDatabaseResults(Event e) {
        try {
            Locale locale = super.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);

            Node node = (Node) e.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/showDatabaseResults.fxml"), bundle);

            Parent root = loader.load();
            DatabaseResultsController ctrl = loader.getController();

            double filterValue;
            try {
                filterValue = Double.valueOf(this.value.getText());
            } catch (Exception ex) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Parsing double value failed. Using default");
                filterValue = 0;
            }
            ctrl.initData(this.date.getValue(), this.debitor.getText(), this.creditor.getText(), filterValue);

            Scene scene = new Scene(root, 800, 600);
            Logger.getLogger(this.getClass()).log(Level.INFO, "loading css files");
            scene.getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
