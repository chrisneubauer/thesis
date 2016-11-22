package de.cneubauer.gui.controller;

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

            FlowPane f = FXMLLoader.load(getClass().getResource("../../../../FXML/settings.fxml"));
            Scene scene = new Scene(f, 600, 400);

            Stage popupStage = new Stage(StageStyle.DECORATED);
            popupStage.setX(stage.getX() + 100);
            popupStage.setY(stage.getY() + 100);
            popupStage.setTitle("Settings");
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(scene);

            popupStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
