package de.cneubauer.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Christoph Neubauer on 23.09.2016.
 * Contains UI logic
 */
public class GUIController {

    @FXML public AnchorPane leftPane;

    @FXML private MenuBar menuBar;

    // this method opens the page where the user can import files
    @FXML
    protected void openScanFormMenu(Event e) {
        try {
            leftPane.getChildren().clear();
            leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/scanForm.fxml")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // this method opens the page where the user can search in the database
    @FXML
    protected void openDatabaseMenu(Event e) {
        try {
            leftPane.getChildren().clear();
            leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/searchDatabase.fxml")));
            /*Node node = (Node) e.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("../../../../FXML/searchDatabase.fxml"));
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //this method saves all states and closes the application
    @FXML
    protected void closeApplication() {
        Platform.exit();
    }

    public void openSettings(ActionEvent e) {
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

    public FXMLLoader changeToResults() {
        try {
            leftPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/showResults.fxml"));
            leftPane.getChildren().add(loader.load());
            return loader;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
