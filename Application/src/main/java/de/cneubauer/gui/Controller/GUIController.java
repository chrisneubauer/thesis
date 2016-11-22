package de.cneubauer.gui.controller;

import de.cneubauer.util.config.Cfg;
import de.cneubauer.util.config.ConfigHelper;
import de.cneubauer.util.enumeration.AppLang;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 23.09.2016.
 * Contains UI logic
 */
public class GUIController {

   // @FXML public AnchorPane leftPane;
    @FXML public SplitPane splitPaneInclude;
    @FXML private SplitPaneController splitPaneIncludeController;

    @FXML private MenuBar menuBar;

    // this method opens the page where the user can import files
    @FXML
    protected void openScanFormMenu(Event e) {
        try {
            this.splitPaneIncludeController.openScanFormMenu(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // this method opens the page where the user can search in the database
    @FXML
    protected void openDatabaseMenu(Event e) {
        try {
            this.splitPaneIncludeController.openDatabaseMenu(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //this method saves all states and closes the application
    @FXML
    protected void closeApplication() {
        Platform.exit();
    }

    public void openSettings() {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();

            Locale locale = this.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/settings.fxml"), bundle);
            FlowPane f = loader.load();
            //FlowPane f = FXMLLoader.load(getClass().getResource("../../../../FXML/settings.fxml"));
            Scene scene = new Scene(f, 600, 400);

            Stage popupStage = new Stage(StageStyle.DECORATED);
            popupStage.setX(stage.getX() + 100);
            popupStage.setY(stage.getY() + 100);
            popupStage.setTitle("Settings");
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(scene);

            SettingsController ctrl = loader.getController();
            ctrl.setPrimaryStage(stage);

            popupStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Locale getCurrentLocale() {
        Locale locale;
        AppLang currentLanguage = AppLang.valueOf(ConfigHelper.getValue(Cfg.APPLICATIONLANGUAGE.getValue()));
        if (currentLanguage.equals(AppLang.GERMAN)) {
            locale = Locale.GERMANY;
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }
}
