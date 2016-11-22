package de.cneubauer.gui.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 * Controller for the Splitpane
 */
public class SplitPaneController extends GUIController {
    @FXML public AnchorPane rightPane;
    @FXML public AnchorPane leftPane;
    @FXML public ImageView pdfImage;

    // this method opens the page where the user can import files
    @FXML
    protected void openScanFormMenu(Event e) {
        try {
            this.leftPane.getChildren().clear();
            this.leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/scanForm.fxml")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void showScanForm() {
        try {
            this.leftPane.getChildren().clear();
            this.leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/scanForm.fxml")));
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
