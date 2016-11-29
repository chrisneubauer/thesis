package de.cneubauer.gui.controller;

import de.cneubauer.util.config.Cfg;
import de.cneubauer.util.config.ConfigHelper;
import de.cneubauer.util.enumeration.AppLang;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 23.09.2016.
 * Contains UI logic
 */
public class GUIController {
    @FXML public SplitPane splitPaneInclude;
    public Button startScanButton;
    public MenuBar menuBar;
    @FXML private SplitPaneController splitPaneIncludeController;

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
            Stage stage = (Stage) startScanButton.getScene().getWindow();

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

    Locale getCurrentLocale() {
        Locale locale;
        AppLang currentLanguage = AppLang.valueOf(ConfigHelper.getValue(Cfg.APPLICATIONLANGUAGE.getValue()));
        if (currentLanguage.equals(AppLang.GERMAN)) {
            locale = Locale.GERMANY;
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    /*
     * Called from start, should open a file dialog to select the directory to be scanned
     * @param   actionEvent     the event from where this method is called
     */
    public void openFileDialog() {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("Select Directory");
        File directory = dir.showDialog(new Stage());
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                this.openProgress(files);
            }
        }
    }

    private void openProgress(File[] files) {
        try {
            Stage stage = (Stage) startScanButton.getScene().getWindow();

            Locale locale = this.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/progress.fxml"), bundle);
            VBox v = loader.load();


            Scene scene = new Scene(v, 1200, 800);
            stage.setScene(scene);

            /*
            Stage popupStage = new Stage(StageStyle.DECORATED);
            popupStage.setX(stage.getX() + 100);
            popupStage.setY(stage.getY() + 100);
            popupStage.setTitle("Settings");
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(scene);*/

            ProgressController ctrl = loader.getController();
            ctrl.initData(files);

            //popupStage.show();
            //ctrl.progressFiles();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
